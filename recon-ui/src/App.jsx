import {
  BrowserRouter,
  Routes,
  Route,
  Link,
  Navigate
} from "react-router-dom";

import PrivateRoute from "./components/PrivateRoute";
import LoginPage from "./pages/LoginPage"; 
import TradesPage from "./pages/TradesPage";
import BreaksPage from "./pages/BreaksPage";

// for toast
import { ToastContainer } from "react-toastify";

import "react-toastify/dist/ReactToastify.css";

function App() {

  return (
    <BrowserRouter>

      <nav>

        <Link to="/trades">
          Trades
        </Link>

        {" | "}

        <Link to="/breaks">
          Breaks
        </Link>

      </nav>


      <hr />
      <ToastContainer />
      <Routes>


      <Route
          path="/trades"
          element={
           <PrivateRoute>
            <TradesPage />
           </PrivateRoute>
          }
      />

      <Route
        path="/breaks"
       element={
        <PrivateRoute>
          <BreaksPage />
        </PrivateRoute>
        }
      />

      
        {/* <Route
          path="/trades"
          element={<TradesPage />}
        />

        <Route
          path="/breaks"
          element={<BreaksPage />}
        /> */}

        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/"
          element={<LoginPage />}
        />

        {/* <Route
          path="/"
          element={<Navigate to="/trades" />}
/> */}

      </Routes>

    </BrowserRouter>
  );
}

export default App;