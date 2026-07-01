import { useEffect, useState } from "react";
import apiClient from "../services/apiClient";

function useBreakSummary() {

  const [data, setData] = useState([]);

  useEffect(() => {

    apiClient
      .get("/recon/breaks/chart/summary")
      .then(res => setData(res.data));

  }, []);

  return data;
}

export default useBreakSummary;