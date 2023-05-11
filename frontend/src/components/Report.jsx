import React from "react";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../stylesheets/Report.css";
import { XCircleFill} from "react-bootstrap-icons";
import MapReport from "./MapReport";

function Report() {

  const navigate = useNavigate();

  const handleClickBack = () => {
    navigate("/home");
  };

  useEffect(() => {
    if(localStorage.getItem("profile") === "Inconvenientes de visión"){
      navigate("/vision");
    }
  }, []);

  return (
    <div className="report-div-container ">
      <MapReport/>
      {<button
        type="button"
        title="Volver"
        className="btn btn-dark back-button"
        onClick={handleClickBack}>
        <XCircleFill className="back-icon" />
      </button>}
    </div>
  );
}

export default Report;