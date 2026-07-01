import {
    useEffect,
    useState
} from "react";

import { getTrades }
from "../services/tradeService";

function useTradeData() {

    const [trades, setTrades] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState(null);

    const loadTrades = async () => {

        try {

            setLoading(true);

            const data =
                await getTrades();

            setTrades(data);

            setError(null);

        } catch (err) {

            if (err.response) {

                setError(
                    `Server Error: ${err.response.status}`
                );

            } else if (err.request) {

                setError(
                    "Backend server unavailable"
                );

            } else {

                setError(err.message);
            }

        } finally {

            setLoading(false);
        }
    };

    useEffect(() => {

        loadTrades();

        const interval =
            setInterval(
                loadTrades,
                30000
            );

        return () =>
            clearInterval(interval);

    }, []);

    return {
        trades,
        loading,
        error
    };
}

export default useTradeData;