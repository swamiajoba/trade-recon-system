import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip, 
   LabelList
} from "recharts";

function BreakSummaryChart({ data }) {

  if (!data || data.length === 0) {
    return (
      <h3>No chart data available</h3>
    );
  }

  return (
    <div
      style={{
        background: "#fff",
        border: "1px solid #ddd",
        borderRadius: "10px",
        padding: "10px",
        marginBottom: "20px",
        maxWidth: "700px"
      }}
    >
      <h2>
        Breaks by Counterparty
      </h2>

      <ResponsiveContainer
        width="100%"
        height={220}
      >
        <BarChart
          data={data}
          layout="vertical"
        >
          <CartesianGrid
            strokeDasharray="3 3"
          />

          <XAxis
            type="number"
          />

          <YAxis
            type="category"
            dataKey="counterpartyId"
          />

          <Tooltip />

          <Bar
            dataKey="breakCount"
            name="Break Count"
            radius={[0, 5, 5, 0]}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default BreakSummaryChart;