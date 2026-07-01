// from with performance and React hook form complient
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import { createTrade } from "../services/tradeService";

function TradeForm() {
    
    // Problem 1 — type="number" with RHF returns a string
    // This is a very common RHF gotcha. HTML <input type="number"> 
    // still gives you a string in the submitted data, not a number. 
    // Your Spring Boot backend expects Integer.

    // Problem 2 — validate for date should use valueAsDate


    // Problem 3 — useCallback on onSubmit is unnecessary with RHF
    // handleSubmit from RHF already memoizes the submit handler internally. 
    // Wrapping onSubmit in useCallback adds complexity with no benefit here 
    // since TradeForm has no children receiving onSubmit as a prop.

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting }
    } = useForm({
        defaultValues: {
            counterpartyId: "",
            instrumentId:   "",
            tradeDate:      "",
            tradeType:      "BUY",
            quantity:       "",
            price:          "",
            status:         "NEW"
        }
    });

    const onSubmit = async (data) => {
        try {
            const createdTrade = await createTrade(data);
            console.log(createdTrade);
            toast.success("Trade created successfully");
            reset();
        } catch (err) {
            toast.error(
                err?.response?.data?.message || "Failed to create trade"
            );
            console.error(err);
        }
    };

    return (
        <div style={{
            minWidth: "320px", padding: "20px",
            border: "1px solid #ddd", borderRadius: "10px",
            boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
        }}>
            <h2>Create Trade</h2>

            {/* noValidate — disable browser native validation, let RHF handle it */}
            <form onSubmit={handleSubmit(onSubmit)} noValidate>

                {/* Counterparty ID */}
                <div style={{ marginBottom: "12px" }}>
                    <label htmlFor="counterpartyId">Counterparty ID</label><br />
                    <input
                        id="counterpartyId"
                        type="number"
                        aria-describedby="counterpartyId-error"
                        aria-invalid={!!errors.counterpartyId}
                        {...register("counterpartyId", {
                            required:     "Counterparty ID is required",
                            valueAsNumber: true,                             // ← returns number not string
                            validate: v => !isNaN(v) || "Must be a valid number"
                        })}
                    />
                    <div id="counterpartyId-error" role="alert" style={{ color: "red", fontSize: "13px" }}>
                        {errors.counterpartyId?.message}
                    </div>
                </div>

                {/* Instrument ID */}
                <div style={{ marginBottom: "12px" }}>
                    <label htmlFor="instrumentId">Instrument ID</label><br />
                    <input
                        id="instrumentId"
                        type="number"
                        aria-describedby="instrumentId-error"
                        aria-invalid={!!errors.instrumentId}
                        {...register("instrumentId", {
                            required:      "Instrument ID is required",
                            valueAsNumber: true,
                            validate: v => !isNaN(v) || "Must be a valid number"
                        })}
                    />
                    <div id="instrumentId-error" role="alert" style={{ color: "red", fontSize: "13px" }}>
                        {errors.instrumentId?.message}
                    </div>
                </div>

                {/* Trade Date */}
                <div style={{ marginBottom: "12px" }}>
                    <label htmlFor="tradeDate">Trade Date</label><br />
                    <input
                        id="tradeDate"
                        type="datetime-local"
                        aria-describedby="tradeDate-error"
                        aria-invalid={!!errors.tradeDate}
                        {...register("tradeDate", {
                            required: "Trade Date is required",
                            validate: (value) => {
                                if (!value) return "Trade Date is required";
                                const selectedDate = new Date(value);
                                const today = new Date();
                                today.setHours(0, 0, 0, 0);
                                return selectedDate >= today ||
                                    "Trade date cannot be before today";
                            }
                        })}
                    />
                    <div id="tradeDate-error" role="alert" style={{ color: "red", fontSize: "13px" }}>
                        {errors.tradeDate?.message}
                    </div>
                </div>

                {/* Trade Type */}
                <div style={{ marginBottom: "12px" }}>
                    <label htmlFor="tradeType">Trade Type</label><br />
                    <select id="tradeType" {...register("tradeType")}>
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                    </select>
                </div>

                {/* Quantity */}
                <div style={{ marginBottom: "12px" }}>
                    <label htmlFor="quantity">Quantity</label><br />
                    <input
                        id="quantity"
                        type="number"
                        aria-describedby="quantity-error"
                        aria-invalid={!!errors.quantity}
                        {...register("quantity", {
                            required:      "Quantity is required",
                            valueAsNumber: true,
                            min:   { value: 1, message: "Quantity must be positive" },
                            validate: v => !isNaN(v) || "Must be a valid number"
                        })}
                    />
                    <div id="quantity-error" role="alert" style={{ color: "red", fontSize: "13px" }}>
                        {errors.quantity?.message}
                    </div>
                </div>

                {/* Price */}
                <div style={{ marginBottom: "12px" }}>
                    <label htmlFor="price">Price</label><br />
                    <input
                        id="price"
                        type="number"
                        step="0.01"
                        aria-describedby="price-error"
                        aria-invalid={!!errors.price}
                        {...register("price", {
                            required:      "Price is required",
                            valueAsNumber: true,
                            min:   { value: 0.01, message: "Price must be positive" },
                            validate: v => !isNaN(v) || "Must be a valid number"
                        })}
                    />
                    <div id="price-error" role="alert" style={{ color: "red", fontSize: "13px" }}>
                        {errors.price?.message}
                    </div>
                </div>

                {/* Status */}
                <div style={{ marginBottom: "20px" }}>
                    <label htmlFor="status">Status</label><br />
                    <select id="status" {...register("status")}>
                        <option value="NEW">NEW</option>
                        <option value="EXECUTED">EXECUTED</option>
                        <option value="CANCELLED">CANCELLED</option>
                    </select>
                </div>

                <button
                    type="submit"
                    disabled={isSubmitting}
                    aria-busy={isSubmitting}
                    aria-label={isSubmitting ? "Submitting trade..." : "Create Trade"}
                >
                    {isSubmitting ? "Submitting…" : "Create Trade"}
                </button>

            </form>
        </div>
    );
}

export default TradeForm;



// form with performance suggestions

// import { useCallback } from "react";
// import { useForm } from "react-hook-form";
// import { toast } from "react-toastify";
// import { createTrade } from "../services/tradeService";

// function TradeForm() {

//     const {
//         register,
//         handleSubmit,
//         reset,
//         formState: { errors, isSubmitting }
//     } = useForm({
//         defaultValues: {
//             counterpartyId: "",
//             instrumentId: "",
//             tradeDate: "",
//             tradeType: "BUY",
//             quantity: "",
//             price: "",
//             status: "NEW"
//         }
//     });

//     // useCallback — stable reference, not recreated on every render
//     const onSubmit = useCallback(async (data) => {
//         try {
//             const createdTrade = await createTrade(data);
//             console.log(createdTrade);
//             toast.success("Trade created successfully");
//             reset();
//         } catch (err) {
//             toast.error(
//                 err?.response?.data?.message || "Failed to create trade"
//             );
//             console.error(err);
//         }
//     }, [reset]);

//     return (
//         <div style={{
//             minWidth: "320px", padding: "20px",
//             border: "1px solid #ddd", borderRadius: "10px",
//             boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
//         }}>
//             <h2>Create Trade</h2>

//             <form onSubmit={handleSubmit(onSubmit)} noValidate>

//                 {/* Counterparty ID */}
//                 <div style={{ marginBottom: "12px" }}>
//                     <label htmlFor="counterpartyId">Counterparty ID</label>
//                     <br />
//                     <input
//                         id="counterpartyId"
//                         type="number"
//                         aria-describedby="counterpartyId-error"
//                         aria-invalid={!!errors.counterpartyId}
//                         {...register("counterpartyId", {
//                             required: "Counterparty ID is required"
//                         })}
//                     />
//                     <div
//                         id="counterpartyId-error"
//                         role="alert"
//                         style={{ color: "red", fontSize: "13px" }}
//                     >
//                         {errors.counterpartyId?.message}
//                     </div>
//                 </div>

//                 {/* Instrument ID */}
//                 <div style={{ marginBottom: "12px" }}>
//                     <label htmlFor="instrumentId">Instrument ID</label>
//                     <br />
//                     <input
//                         id="instrumentId"
//                         type="number"
//                         aria-describedby="instrumentId-error"
//                         aria-invalid={!!errors.instrumentId}
//                         {...register("instrumentId", {
//                             required: "Instrument ID is required"
//                         })}
//                     />
//                     <div
//                         id="instrumentId-error"
//                         role="alert"
//                         style={{ color: "red", fontSize: "13px" }}
//                     >
//                         {errors.instrumentId?.message}
//                     </div>
//                 </div>

//                 {/* Trade Date */}
//                 <div style={{ marginBottom: "12px" }}>
//                     <label htmlFor="tradeDate">Trade Date</label>
//                     <br />
//                     <input
//                         id="tradeDate"
//                         type="datetime-local"
//                         aria-describedby="tradeDate-error"
//                         aria-invalid={!!errors.tradeDate}
//                         {...register("tradeDate", {
//                             required: "Trade Date is required",
//                             validate: (value) => {
//                                 const selectedDate = new Date(value);
//                                 const today = new Date();
//                                 today.setHours(0, 0, 0, 0);
//                                 return (
//                                     selectedDate >= today ||
//                                     "Trade date cannot be before today"
//                                 );
//                             }
//                         })}
//                     />
//                     <div
//                         id="tradeDate-error"
//                         role="alert"
//                         style={{ color: "red", fontSize: "13px" }}
//                     >
//                         {errors.tradeDate?.message}
//                     </div>
//                 </div>

//                 {/* Trade Type */}
//                 <div style={{ marginBottom: "12px" }}>
//                     <label htmlFor="tradeType">Trade Type</label>
//                     <br />
//                     <select id="tradeType" {...register("tradeType")}>
//                         <option value="BUY">BUY</option>
//                         <option value="SELL">SELL</option>
//                     </select>
//                 </div>

//                 {/* Quantity */}
//                 <div style={{ marginBottom: "12px" }}>
//                     <label htmlFor="quantity">Quantity</label>
//                     <br />
//                     <input
//                         id="quantity"
//                         type="number"
//                         aria-describedby="quantity-error"
//                         aria-invalid={!!errors.quantity}
//                         {...register("quantity", {
//                             required: "Quantity is required",
//                             min: { value: 1, message: "Quantity must be positive" }
//                         })}
//                     />
//                     <div
//                         id="quantity-error"
//                         role="alert"
//                         style={{ color: "red", fontSize: "13px" }}
//                     >
//                         {errors.quantity?.message}
//                     </div>
//                 </div>

//                 {/* Price */}
//                 <div style={{ marginBottom: "12px" }}>
//                     <label htmlFor="price">Price</label>
//                     <br />
//                     <input
//                         id="price"
//                         type="number"
//                         step="0.01"
//                         aria-describedby="price-error"
//                         aria-invalid={!!errors.price}
//                         {...register("price", {
//                             required: "Price is required",
//                             min: { value: 0.01, message: "Price must be positive" }
//                         })}
//                     />
//                     <div
//                         id="price-error"
//                         role="alert"
//                         style={{ color: "red", fontSize: "13px" }}
//                     >
//                         {errors.price?.message}
//                     </div>
//                 </div>

//                 {/* Status */}
//                 <div style={{ marginBottom: "20px" }}>
//                     <label htmlFor="status">Status</label>
//                     <br />
//                     <select id="status" {...register("status")}>
//                         <option value="NEW">NEW</option>
//                         <option value="EXECUTED">EXECUTED</option>
//                         <option value="CANCELLED">CANCELLED</option>
//                     </select>
//                 </div>

//                 <button
//                     type="submit"
//                     disabled={isSubmitting}
//                     aria-busy={isSubmitting}
//                     aria-label={isSubmitting ? "Submitting trade..." : "Create Trade"}
//                 >
//                     {isSubmitting ? "Submitting…" : "Create Trade"}
//                 </button>

//             </form>
//         </div>
//     );
// }

// export default TradeForm;