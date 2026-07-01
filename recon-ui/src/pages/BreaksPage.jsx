import BreakBadge from "../components/BreakBadge";
import StatCard from "../components/StatCard";
import useBreaks from "../hooks/useBreaks";
import useBreakSummary from "../hooks/useBreakSummary";
import { useState, useReducer, useEffect, useRef , useCallback} from "react";
import LoadingSpinner from "../components/LoadingSpinner";
import BreakSummaryChart from "../components/BreakSummaryChart";
import { resolveBreak } from "../services/breakService";
import { toast } from "react-toastify";
import useWebSocket from "../hooks/useWebSocket";
import withAuditLog from "../hooks/withAuditLog";

function breakReducer(state, action) {
    switch (action.type) {
        case "INIT":       return { openCount: action.payload };
        case "RESOLVE_BREAK": return { openCount: state.openCount - 1 };
        default:           return state;
    }
}
// BreaksPage now receives logAction as a prop from the HOC
function BreaksPage({ logAction }) {

    const [selectedStatus, setSelectedStatus] = useState("OPEN");
    const { breaks, setBreaks, loading, error } = useBreaks(selectedStatus);
    const summary = useBreakSummary();

    // Modal state
    const [selectedBreak, setSelectedBreak] = useState(null);  // null = closed
    const [note, setNote]                   = useState("");
    const [resolving, setResolving]         = useState(false);

    

    const [breakState, dispatch] = useReducer(breakReducer, { openCount: 0 });

    // Capture current openCount in a ref so rollback isn't a stale closure
    const openCountRef = useRef(0);
    openCountRef.current = breakState.openCount;

    useEffect(() => {
        dispatch({
            type: "INIT",
            payload: breaks.filter(b => b.status === "OPEN").length
        });
    }, [breaks]);

    // Called when user clicks Resolve button on a row
    const openResolveModal = (breakItem) => {
        setSelectedBreak(breakItem);
        setNote("");
    };

    const closeModal = () => {
        setSelectedBreak(null);
        setNote("");
    };

    // Called when user confirms inside modal
    const handleResolve = async () => {
        const breakId = selectedBreak.breakId;
        const savedNote = note;


         // Log the action BEFORE the optimistic update
        logAction({
            action:     "RESOLVE",
            entityType: "RECON_BREAK",
            entityId:   breakId,
            oldValue:   { status: "OPEN" },
            newValue:   { status: "RESOLVED", note: savedNote }
        });

        // 1. Optimistic update
        setBreaks(prev =>
            prev.map(b =>
                b.breakId === breakId ? { ...b, status: "RESOLVED" } : b
            )
        );
        dispatch({ type: "RESOLVE_BREAK" });

        const countBeforeResolve = openCountRef.current; // captured before dispatch

        closeModal();
        setResolving(true);

        try {
            await resolveBreak(breakId, savedNote);
        } catch (err) {
            console.error(err);
            alert("Failed to resolve break. Rolling back.");

            logAction({
                action:     "RESOLVE_ROLLBACK",
                entityType: "RECON_BREAK",
                entityId:   breakId,
                oldValue:   { status: "RESOLVED" },
                newValue:   { status: "OPEN" }
            });

            // Rollback
            setBreaks(prev =>
                prev.map(b =>
                    b.breakId === breakId ? { ...b, status: "OPEN" } : b
                )
            );
            dispatch({ type: "INIT", payload: countBeforeResolve });
        } finally {
            setResolving(false);
        }
    };

  // Filter change — log when analyst switches OPEN/RESOLVED tab
    const handleStatusChange = (newStatus) => {

        logAction({
            action:     "FILTER",
            entityType: "RECON_BREAK",
            entityId:   "0",
            newValue:   { status: newStatus }
        });

        setSelectedStatus(newStatus);
    };


// for websocket
const handleBreakUpdate = useCallback((message) => {

                // message shape from your BreakResolvedPublisher:
                // { breakId: 7, tradeId: 101, status: "RESOLVED", eventType: "BREAK_RESOLVED" }

                if (message.eventType === "BREAK_RESOLVED") {

                    setBreaks(prev =>
                        prev.map(b =>
                            b.breakId === message.breakId
                                ? { ...b, status: "RESOLVED" }
                                : b
                        )
                    );

                    dispatch({ type: "RESOLVE_BREAK" });

                    toast.info(`Break #${message.breakId} resolved`);
                }

            }, []);


   const { connected } = useWebSocket("/topic/breaks", handleBreakUpdate);  // after resolving update page by receiving wesocker message

    if (loading) return <LoadingSpinner />;
    if (error)   return <h2>Error: {error}</h2>;

    return (
        <div>
            <h1>Reconciliation Breaks</h1>

        <div style={{
                display: "flex",
                alignItems: "center",
                gap: "6px",
                marginBottom: "12px"
            }}>
                <div style={{
                    width: "8px",
                    height: "8px",
                    borderRadius: "50%",
                    background: connected ? "#16a34a" : "#dc2626",
                    boxShadow: connected ? "0 0 6px #16a34a" : "none"
                }} />
                <span style={{ fontSize: "12px", color: "#6b7280" }}>
                    {connected ? "Live updates active" : "Reconnecting…"}
                </span>
            </div>
            <BreakSummaryChart data={summary} />

            <div style={{ marginBottom: "20px" }}>
                <label>
                    <input
                        type="radio" value="OPEN"
                        checked={selectedStatus === "OPEN"}
                        onChange={e => handleStatusChange(e.target.value)}
                    />
                    {" "}Open Breaks
                </label>
                {"  "}
                <label>
                    <input
                        type="radio" value="RESOLVED"
                        checked={selectedStatus === "RESOLVED"}
                        onChange={e => handleStatusChange(e.target.value)}
                    />
                    {" "}Resolved Breaks
                </label>
            </div>

            <StatCard label="Open Breaks" value={breakState.openCount} />

            <table border="1" cellPadding="10">
                <thead>
                    <tr>
                        <th>Break ID</th><th>Trade ID</th><th>Break Type</th>
                        <th>Our Qty</th><th>Their Qty</th>
                        <th>Our Amount</th><th>Their Amount</th>
                        <th>Status</th><th>Resolved By</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {breaks.map(item => (
                        <tr key={item.breakId}>
                            <td>{item.breakId}</td>
                            <td>{item.tradeId}</td>
                            <td>{item.breakType}</td>
                            <td>{item.ourQty}</td>
                            <td>{item.theirQty}</td>
                            <td>{item.ourAmount}</td>
                            <td>{item.theirAmount}</td>
                            <td><BreakBadge status={item.status} /></td>
                            <td>{item.resolvedBy ?? "-"}</td>
                            <td>
                                {item.status === "OPEN" && (
                                    <button
                                        onClick={() => openResolveModal(item)}
                                        style={resolveButtonStyle}
                                        onMouseEnter={e => Object.assign(e.target.style, resolveButtonHover)}
                                        onMouseLeave={e => Object.assign(e.target.style, resolveButtonStyle)}
                                    >
                                        ✓ Resolve
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* ── MODAL ── */}
            {selectedBreak && (
                <div style={overlayStyle} onClick={closeModal}>
                    <div style={modalStyle} onClick={e => e.stopPropagation()}>

                        {/* Header */}
                        <div style={modalHeaderStyle}>
                            <h2 style={{ margin: 0, fontSize: "16px", fontWeight: 500 }}>
                                Resolve break
                            </h2>
                            <button onClick={closeModal} style={closeBtnStyle}>✕</button>
                        </div>

                        {/* Break details grid */}
                        <div style={detailGridStyle}>
                            {[
                                ["Break ID",    selectedBreak.breakId],
                                ["Trade ID",    selectedBreak.tradeId],
                                ["Break Type",  selectedBreak.breakType],
                                ["Status",      selectedBreak.status],
                                ["Our Qty",     selectedBreak.ourQty],
                                ["Their Qty",   selectedBreak.theirQty],
                                ["Our Amount",  selectedBreak.ourAmount],
                                ["Their Amount",selectedBreak.theirAmount],
                            ].map(([label, value]) => (
                                <div key={label} style={detailItemStyle}>
                                    <div style={detailLabelStyle}>{label}</div>
                                    <div style={detailValueStyle}>{value}</div>
                                </div>
                            ))}
                        </div>

                        {/* Note input */}
                        <label style={{ fontSize: "13px", color: "#6b7280", display: "block", marginBottom: "6px" }}>
                            Resolution note <span style={{ color: "#9ca3af" }}>(optional)</span>
                        </label>
                        <textarea
                            value={note}
                            onChange={e => setNote(e.target.value)}
                            placeholder="e.g. Confirmed with custodian — partial fill on EOD batch"
                            rows={3}
                            style={textareaStyle}
                        />

                        {/* Actions */}
                        <div style={{ display: "flex", justifyContent: "flex-end", gap: "10px", marginTop: "16px" }}>
                            <button onClick={closeModal} style={cancelBtnStyle}>Cancel</button>
                            <button
                                onClick={handleResolve}
                                disabled={resolving}
                                style={confirmBtnStyle}
                            >
                                {resolving ? "Resolving…" : "✓ Confirm resolve"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}


// ── Inline styles ──────────────────────────────────────────────────────────

const resolveButtonStyle = {
    background: "#0F6E56",
    color: "#E1F5EE",
    border: "none",
    borderRadius: "6px",
    padding: "5px 12px",
    fontSize: "13px",
    fontWeight: 500,
    cursor: "pointer",
    transition: "background 0.15s",
};
const resolveButtonHover = { ...resolveButtonStyle, background: "#085041" };

const overlayStyle = {
    position: "fixed", inset: 0,
    background: "rgba(0,0,0,0.45)",
    display: "flex", alignItems: "center", justifyContent: "center",
    zIndex: 1000,
};
const modalStyle = {
    background: "#fff",
    borderRadius: "12px",
    border: "0.5px solid #e5e7eb",
    padding: "1.5rem",
    width: "100%",
    maxWidth: "480px",
    boxSizing: "border-box",
};
const modalHeaderStyle = {
    display: "flex", justifyContent: "space-between", alignItems: "center",
    marginBottom: "1.25rem",
};
const closeBtnStyle = {
    background: "none", border: "none", cursor: "pointer",
    fontSize: "18px", color: "#6b7280", lineHeight: 1, padding: "2px 6px",
    borderRadius: "4px",
};
const detailGridStyle = {
    display: "grid", gridTemplateColumns: "1fr 1fr",
    gap: "10px", marginBottom: "1.25rem",
};
const detailItemStyle = {
    background: "#f9fafb", borderRadius: "8px", padding: "10px 12px",
};
const detailLabelStyle = {
    fontSize: "11px", color: "#9ca3af",
    textTransform: "uppercase", letterSpacing: "0.05em", marginBottom: "2px",
};
const detailValueStyle = {
    fontSize: "14px", fontWeight: 500, color: "#111827",
};
const textareaStyle = {
    width: "100%", boxSizing: "border-box", resize: "vertical",
    fontSize: "14px", minHeight: "72px",
    border: "0.5px solid #d1d5db", borderRadius: "8px",
    padding: "10px 12px", fontFamily: "inherit", color: "#67e6e9",
};
const cancelBtnStyle = {
    background: "none", border: "0.5px solid #d1d5db", borderRadius: "8px",
    padding: "8px 18px", fontSize: "14px", color: "#6b7280", cursor: "pointer",
};
const confirmBtnStyle = {
    background: "#0F6E56", border: "none", borderRadius: "8px",
    padding: "8px 20px", fontSize: "14px", fontWeight: 500,
    color: "#E1F5EE", cursor: "pointer",
};

//export default BreaksPage;


// Wrap with HOC at the bottom — not inside the component
export default withAuditLog(BreaksPage);





// Normal Resolve Button without modal

// import BreakBadge from "../components/BreakBadge";
// import StatCard from "../components/StatCard";
// import useBreaks
// from "../hooks/useBreaks";

// import useBreakSummary from "../hooks/useBreakSummary";

// import { useState,useReducer,useEffect } from "react";

// import LoadingSpinner
// from "../components/LoadingSpinner";

// import BreakSummaryChart from "../components/BreakSummaryChart";

// import {
//   resolveBreak
// }
// from "../services/breakService";

// function BreaksPage() {

//   const [selectedStatus, setSelectedStatus] = useState("OPEN");

//     const {
//       breaks,
//       setBreaks,
//       loading,
//       error
//     }
//     =
//     useBreaks( selectedStatus );

//   const summary = useBreakSummary();

//   // Modal state
//   const [ selectedBreak, setSelectedBreak ] = useState(null);

//   // for resolving break 
  
//   function breakReducer(
//   state,
//   action
// ) {

//   switch (action.type) {

//     case "INIT":

//       return {
//         openCount:
//           action.payload
//       };

//     case "RESOLVE_BREAK":

//       return {
//         openCount:
//           state.openCount - 1
//       };

//     default:
//       return state;
//   }
// }

// // Initialize reducer
// const [breakState, dispatch] =
//   useReducer(
//     breakReducer,
//     {
//       openCount: 0
//     }
//   );

//   // for breks which are open
//   useEffect(() => {

//   dispatch({
//     type: "INIT",
//     payload:
//       breaks.filter(
//         b =>
//           b.status === "OPEN"
//       ).length
//   });

// }, [breaks]);

// // for handling resolve break 
// const handleResolve = async (
//     breakItem
// ) => {

//     const breakId =
//         breakItem.breakId;

//     //
//     // Optimistic Update
//     //

//     setBreaks(prev =>

//         prev.map(b =>

//             b.breakId === breakId

//                 ? {
//                     ...b,
//                     status: "RESOLVED"
//                   }

//                 : b
//         )
//     );

//     dispatch({
//         type:
//             "RESOLVE_BREAK"
//     });

//     try {

//         await resolveBreak(
//             breakId
//         );

//     } catch (err) {

//         console.error(err);

//         alert(
//             "Failed to resolve break"
//         );

//         //
//         // Rollback
//         //

//         setBreaks(prev =>

//             prev.map(b =>

//                 b.breakId === breakId

//                     ? {
//                         ...b,
//                         status: "OPEN"
//                       }

//                     : b
//             )
//         );

//         dispatch({
//             type:
//                 "INIT",
//             payload:
//                 breaks.filter(
//                     b =>
//                         b.status ===
//                         "OPEN"
//                 ).length
//         });
//     }
// };


// // for loading data
//   if (loading) {

//     return <LoadingSpinner />;
//   }

//   if (error) {

//     return (
//       <h2>
//         Error: {error}
//       </h2>
//     );
//   }

//   return (
//     <div>
//       <h1>Reconciliation Breaks</h1>

//       <BreakSummaryChart data={summary} />

//       <div style={{ marginBottom: "20px" }}>

//       <label>
//         <input
//           type="radio"
//           value="OPEN"
//           checked={selectedStatus === "OPEN"}
//           onChange={(e) =>
//             setSelectedStatus(e.target.value)
//           }
//         />
//         Open Breaks
//       </label>

//       {"  "}

//       <label>
//         <input
//           type="radio"
//           value="RESOLVED"
//           checked={selectedStatus === "RESOLVED"}
//           onChange={(e) =>
//             setSelectedStatus(e.target.value)
//           }
//         />
//         Resolved Breaks
//       </label>

//     </div>
    
//       {/* <StatCard
//       label={`${selectedStatus} Breaks`}
//       value={breaks.length}
//         /> */}

//         <StatCard
//           label="Open Breaks"
//           value={
//             breakState.openCount
//           }
//         />

//       <table border="1" cellPadding="10">

//         <thead>
//           <tr>
//             <th>Break ID</th>
//             <th>Trade ID</th>
//             <th>Break Type</th>
//             <th>Our Qty</th>
//             <th>Their Qty</th>
//             <th>Our Amount</th>
//             <th>Their Amount</th>
//             <th>Status</th>
//             <th>Resolved By</th>
//             <th>Action</th>
//           </tr>
//         </thead>

//         <tbody>

//           {breaks.map((item) => (
//             <tr key={item.breakId}>

//               <td>{item.breakId}</td>

//               <td>{item.tradeId}</td>

//               <td>{item.breakType}</td>

//               <td>{item.ourQty}</td>

//               <td>{item.theirQty}</td>

//               <td>{item.ourAmount}</td>

//               <td>{item.theirAmount}</td>

//               <td>
//                 <BreakBadge status={item.status} />
//               </td>

//               <td>
//                 {item.resolvedBy ?? "-"}
//               </td>
//               <td>

//                 {
//                   item.status === "OPEN" && (

//                     <button
//                       onClick={() =>
//                         handleResolve(item)
//                       }
//                     >
//                       Resolve
//                     </button>

//                   )
//                 }

//               </td>

//             </tr>
//           ))}

//         </tbody>

//       </table>

//       {selectedBreak && (
//                 <div>
//                   Selected Break:
//                   {selectedBreak.breakId}
//                 </div>
//               )}
//     </div>
//   );
// }

// export default BreaksPage;