import React from "react";
import { useEffect } from "react";
import "../stylesheets/Path.css"
import { useNavigate } from "react-router-dom";
import { XCircleFill } from "react-bootstrap-icons";
import MapPath from "./MapPath";
import { useSpeechSynthesis } from "react-speech-kit";

function Path() {

  const navigate = useNavigate();
  const { speak, cancel } = useSpeechSynthesis();

  const handleClickBack = () => {
    cancel();
    navigate("/home");
  };

  useEffect(() => {
    if (localStorage.getItem("profile") === "Inconvenientes de visión") {
      navigate("/home");
    }
  }, []);

  return (
    <div className="path-div-container">
      <MapPath />
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

export default Path;