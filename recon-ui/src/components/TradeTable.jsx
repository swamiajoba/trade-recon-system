import { memo, useMemo } from "react";

// React.memo — only re-renders if trades prop actually changes
const TradeTable = memo(function TradeTable({ trades = [] }) {

    // useMemo — sort/filter trades without recomputing on every render
    // Example: sort by tradeId descending (most recent first)
    const sortedTrades = useMemo(() =>
        [...trades].sort((a, b) => b.tradeId - a.tradeId),
        [trades]
    );

    // Empty state — no silent blank table
    if (sortedTrades.length === 0) {
        return (
            <p
                role="status"
                aria-live="polite"
                style={{ color: "#6b7280", marginTop: "12px" }}
            >
                No trades found.
            </p>
        );
    }

    return (
        <table
            border="1"
            cellPadding="10"
            aria-label="Trade blotter"      // accessibility fix
        >
            <thead>
                <tr>
                    <th scope="col">Trade ID</th>
                    <th scope="col">Counterparty</th>
                    <th scope="col">Instrument</th>
                    <th scope="col">Qty</th>
                    <th scope="col">Price</th>
                    <th scope="col">Type</th>
                    <th scope="col">Status</th>
                </tr>
            </thead>
            <tbody>
                {sortedTrades.map(trade => (
                    <tr key={trade.tradeId}>
                        <td>{trade.tradeId}</td>
                        <td>{trade.counterpartyId}</td>
                        <td>{trade.instrumentId}</td>
                        <td>{trade.quantity}</td>
                        <td>{trade.price}</td>
                        <td>{trade.tradeType}</td>
                        <td>{trade.status}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
});

export default TradeTable;