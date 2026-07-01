import { useState } from "react";
import { login } from "../services/authService";
import { getRoleFromToken } from "../utils/jwtUtils";

function LoginPage() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError]       = useState("");

    const handleLogin = async (e) => {

        e.preventDefault();

        try {

            const data = await login(username, password);

            // Store token
            localStorage.setItem("jwtToken", data.token);

            // Decode role from token
            const role = getRoleFromToken(data.token);

            // Redirect based on role
            if (role === "ADMIN") {
                window.location.href = "/trades";    // admin sees trades
            } else if (role === "OPS") {
                window.location.href = "/breaks";    // ops sees recon breaks
            } 
            // else {
            //     window.location.href = "/dashboard"; // fallback
            // }

        } catch (err) {
            setError("Invalid Credentials");
        }
    };

    return (
        <form onSubmit={handleLogin}>

            <input
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Username"
            />

            <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Password"
            />

            <button type="submit">Login</button>

            <p style={{ color: "red" }}>{error}</p>

        </form>
    );
}

export default LoginPage;