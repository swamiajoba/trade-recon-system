import apiClient from "./apiClient";

export const getTrades = async () => {

    const response =
        await apiClient.get("/trades");

    return response.data;
};

export const createTrade = async (
    trade
) => {

    const response =
        await apiClient.post(
            "/trades",
            trade
        );

    return response.data;
};