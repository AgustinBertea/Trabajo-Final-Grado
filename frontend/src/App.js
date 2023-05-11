import "./stylesheets/App.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import Home from "./components/Home";
import Gateway from "./components/Gateway";
import Register from "./components/Register";
import ForgotPassword from "./components/ForgotPassword";
import MyAccount from "./components/MyAccount";
import Summary from "./components/Summary";
import Report from "./components/Report";
import Path from "./components/Path";
import Vision from "./components/Vision";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          {/* Routes for not loged users */}
          <Route element={<Gateway tokenIsExpected={false} redirectTo="/home" />}>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
          </Route>
          
          {/* Routes for loged users */}
          <Route element={<Gateway tokenIsExpected={true} />}>
            <Route path="/home" element={<Home />} />
            <Route path="/my-account" element={<MyAccount />} />
            <Route path="/summary" element={<Summary />} />
            <Route path="/report" element={<Report />} />
            <Route path="/path" element={<Path />} />
            <Route path="/vision" element={<Vision />} />
            <Route path="*" element={ <Navigate replace to="/home" /> } />
          </Route>

        </Routes>
      </BrowserRouter>
    </div>
  ); 
}

export default App;