import {
    useEffect,
    useState
} from "react";

import {
    getBreaks
}
from "../services/breakService";

function useBreaks(status) {

    const [breaks, setBreaks] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState(null);

    const loadBreaks = async () => {

        try {

            setLoading(true);

            const data =
                await getBreaks(status);

            setBreaks(data);

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

        loadBreaks();

        // const interval =
        //     setInterval(
        //         loadBreaks,
        //         30000
        //     );

        // return () =>
        //     clearInterval(interval);

    }, [status]);

   return {
    breaks,
    setBreaks,
    loading,
    error
    };
}

export default useBreaks;