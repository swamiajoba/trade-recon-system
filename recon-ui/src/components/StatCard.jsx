function StatCard({ label, value }) {
  return (
    <div
      style={{
        backgroundColor: "#e0abab",
        borderRadius: "12px",
        padding: "20px",
        width: "220px",
        boxShadow:
          "0 4px 12px rgba(0,0,0,0.08)",
        borderTop: "4px solid #0f172a",
        transition: "0.3s ease"
      }}
    >
      <div
        style={{
          color: "#64748b",
          fontSize: "14px",
          fontWeight: "600",
          textTransform: "uppercase",
          marginBottom: "12px"
        }}
      >
        {label}
      </div>

      <div
        style={{
          fontSize: "36px",
          fontWeight: "700",
          color: "#0f172a"
        }}
      >
        {value}
      </div>
    </div>
  );
}

export default StatCard;





// function StatCard({ label, value }) {

//   return (
//     <div
//       style={{
//         border: "1px solid #ddd",
//         borderRadius: "10px",
//         padding: "20px",
//         width: "220px"
//       }}
//     >
//       <h4>{label}</h4>

//       <h1>{value}</h1>
//     </div>
//   );
// }

// export default StatCard;