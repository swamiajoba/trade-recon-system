// for sprint 3
import { useMemo, useState } from "react";

import TradeTable from "../components/TradeTable";
import LoadingSpinner from "../components/LoadingSpinner";
import StatCard from "../components/StatCard";

import useTradeData from "../hooks/useTradeData";
import apiClient from "../services/apiClient";


import TradeForm from "../components/TradeForm";

function TradesPage() {

  const {
    trades,
    loading,
    error
  } = useTradeData();

 
  const tradeSummary = useMemo(() => {

    return trades.reduce(
      (acc, trade) => {

        acc.tradeCount++;

        acc.totalAmount +=
          trade.quantity * trade.price;

        return acc;

      },
      {
        tradeCount: 0,
        totalAmount: 0
      }
    );

  }, [trades]);

 

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <h2>
        Error: {error}
      </h2>
    );
  }

  return (
    <div>

      <h1>Trades Dashboard</h1>

      <div
        style={{
          display: "flex",
          gap: "20px",
          marginBottom: "20px"
        }}
      >
        <StatCard
          label="Total Trades"
          value={tradeSummary.tradeCount}
          />

        <StatCard
          label="Total Amount"
          value={
            tradeSummary.totalAmount.toLocaleString(
              "en-IN",
              {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
              }
            )
          }
        />
      </div>

   
      <div
        style={{
          display: "flex",
          gap: "30px",
          alignItems: "flex-start"
        }}
      >

        {/* CREATE TRADE FORM */}

        <TradeForm/>
     
        {/* TRADE TABLE */}

        <div
          style={{
            flex: 1,
            overflowX: "auto"
          }}
        >
          <TradeTable trades={trades} />
        </div>

      </div>

    </div>
  );
}

export default TradesPage;





//sprint 2 data

// import TradeTable
// from "../components/TradeTable";

// import LoadingSpinner
// from "../components/LoadingSpinner";

// import useTradeData
// from "../hooks/useTradeData";

// import { useMemo } from "react";

// import StatCard from "../components/StatCard";

// function TradesPage() {

//   const {
//     trades,
//     loading,
//     error
//   } = useTradeData();

//     const tradeSummary = useMemo(() => {

//     return trades.reduce(
//       (acc, trade) => {

//         acc.tradeCount++;

//         acc.totalAmount +=
//           trade.quantity * trade.price;

//         return acc;

//       },
//       {
//         tradeCount: 0,
//         totalAmount: 0
//       }
//     );

//   }, [trades]);


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
//     <>
//       <h1>Trades</h1>
    
//          <div
//         style={{
//           display: "flex",
//           gap: "20px",
//           marginBottom: "20px"
//         }}
//       >
//         <StatCard
//           label="Total Trades"
//           value={tradeSummary.tradeCount}
//         />

//         <StatCard
//           label="Total Amount"
//           value={tradeSummary.totalAmount.toFixed(2)}
//         />
//       </div>

//       <TradeTable
//         trades={trades}
//       />
//     </>
//   );
// }

// export default TradesPage;