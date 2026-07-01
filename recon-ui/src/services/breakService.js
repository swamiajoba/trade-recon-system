import apiClient from "./apiClient";

export const getBreaks = async (status) => {

    const response =
        await apiClient.get(
            `/recon/breaks?status=${status}`
        );

    return response.data;
};


export const resolveBreak = async (breakId, note) => {
    const response = await apiClient.put(
        `/recon/breaks/${breakId}/resolve`,
        { note }           // ← was missing; Sprint spec implies note goes to backend
    );
    return response.data;
};


// export const resolveBreak = async ( breakId, noteb ) => {

//     const response =
//         await apiClient.put(
//             `/recon/breaks/${breakId}/resolve`
//         );

//     return response.data;
// };