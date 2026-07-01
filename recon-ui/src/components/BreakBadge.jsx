function BreakBadge({ status }) {

  const colors = {
    OPEN: "#dc3545",
    RESOLVED: "#198754"
  };

  return (
    <span
      style={{
        backgroundColor: colors[status] || "#6c757d",
        color: "white",
        padding: "5px 10px",
        borderRadius: "10px"
      }}
    >
      {status}
    </span>
  );
}

export default BreakBadge;