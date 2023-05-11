import React from "react";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import MapView from "./MapView";
import "../stylesheets/Home.css";
import { PersonFillGear, Clipboard2PulseFill, SignTurnSlightRightFill, FlagFill } from "react-bootstrap-icons";

function Home() {

  const navigate = useNavigate();

  const handleClickMyAccount = () => {
    navigate("/my-account");
  };

  const handleClickSummary = () => {
    navigate("/summary");
  };

  const handleClickReport = () => {
    navigate("/report");
  };

  const handleShortestPath = () => {
    navigate("/path");
  };

  useEffect(() => {
    if(localStorage.getItem("profile") === "Inconvenientes de visión"){
      navigate("/vision");
    }
  }, []);

  return (
    <div className="home-div-container ">
      <MapView />
      {<button
        type="button"
        title="Opciones de usuario"
        className="btn btn-dark options-button"
        onClick={handleClickMyAccount}>
        <PersonFillGear className="options-icon" />
      </button>}
      {<button
        type="button"
        title="Descargar un resumen de situación"
        className="btn btn-dark summary-button"
        onClick={handleClickSummary}>
        <Clipboard2PulseFill className="summary-icon" />
      </button>}
      {<button
        type="button"
        title="Reportar un hito"
        className="btn btn-dark report-button"
        onClick={handleClickReport}>
        <FlagFill className="report-icon" />
      </button>}
      {<button
        type="button"
        title="Encontrar el camino más accesible"
        className="btn btn-dark shortest-path-button"
        onClick={handleShortestPath}>
        <SignTurnSlightRightFill className="shortest-path-icon" />
      </button>}

    </div>
  );
}

export default Home;