import React from "react";
import { useState, useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import "../stylesheets/MapReport.css";
import { graphUrl } from "../services/api-rest";
import Axios from "axios";
import iconAuditiveAlert from "../images/auditiveAlert.png";
import iconBadCondition from "../images/badConditions.png";
import iconBlocking from "../images/blocking.png";
import iconCrosswalkMissing from "../images/crosswalkMissing.png";
import iconPodotactile from "../images/podotactile.png";
import iconRampMissing from "../images/rampMissing.png";
import iconLocation from "../images/location.png";
import iconCircle from "../images/circle.png";

function SetViewOnClick({ coords }) {
  const map = useMap();
  map.setView(coords, map.getZoom());

  return null;
}

function MapReport() {
  const [roads, setRoads] = useState([]);
  const [map, setMap] = useState();
  const [data, setData] = useState();
  const [location, setLocation] = useState([-31.4133, -64.1840]);
  const [locationLatitude, setLocationLatitude] = useState(-31.4133);
  const [locationLongitude, setLocationLongitude] = useState(-64.1840);

  useEffect(() => {
    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setLocation([latitude, longitude]);
        setLocationLatitude(latitude);
        setLocationLongitude(longitude);
      },
      (error) => {
        console.log(error);
      }
    );

    return () => {
      navigator.geolocation.clearWatch(watchId);
    };
  }, []);

  const iconUserLocation = new L.icon({
    iconUrl: iconLocation,
    iconSize: [40, 40],
    iconAnchor: [15, 15],
    className: 'rounded-icon'
  });

  useEffect(() => {
    const fetchRoads = () => {
      const url = graphUrl + "/graph/roads-all";
      Axios.get(url)
        .then((response) => {
          setRoads(response.data);
        })
        .catch((error) => {
          console.log(error)
        });
    };
    fetchRoads();
  }, [data]);

  useEffect(() => {
    setMap(
      <>
        {roads.length !== 0 ? null : <div className="shadow loading-div"> Cargando... </div>}
        <MapContainer
          center={[locationLatitude, locationLongitude]}
          zoom={18}
          minZoom={18}
          scrollWheelZoom={true}
          className="map-container map-zoom-default" >
          <TileLayer
            attribution='Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012'
            url='https://{s}.basemaps.cartocdn.com/rastertiles/voyager_labels_under/{z}/{x}/{y}{r}.png'
          />

          {location && <Marker key={localStorage.getItem("userId")} zIndexOffset={100} position={location} icon={iconUserLocation}></Marker>}

          {roads.length !== 0 ? showRoadMarkers() : null}

          <SetViewOnClick
            coords={[
              locationLatitude,
              locationLongitude
            ]}
          />

        </MapContainer>
      </>
    );
  }, [location, roads]);

  const showRoadMarkers = () => {
    const iconRoad = new L.icon({
      iconUrl: iconCircle,
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });
    return (roads.map(road => (
      <Marker key={road.id} position={[road.roadCenterLatitude, road.roadCenterLongitude]} icon={iconRoad}>
        <Popup>
          <div className="marker-div">
            {road.type !== "Senda peatonal" && !road.blockingExists
              && !road.badConditionExists && !road.podotactileExists
              ||
              road.type === "Senda peatonal" && !road.rampMissingExists
              && !road.blockingExists && !road.crosswalkMissingExists
              && !road.auditiveAlertExists && !road.badConditionExists
              ? null : "Reportado para este camino"}

            {(road.rampMissingExists && road.type === "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconRampMissing} /></div>

                {road.rampMissingIdUserReport === localStorage.getItem("userId") ?
                  <div className="col-8"><button onClick={() => deleteMilestone(road.id, "ramps-missing")} className="btn btn-danger btn-report">Eliminar</button></div>
                  :
                  <>
                    {
                      road.rampMissingIdsUserPositiveVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "ramps-missing")} className="btn btn-success btn-voted">Existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addPositiveVote(road.id, "ramps-missing")} className="btn btn-success">Existe</button></div>
                    }
                    {
                      road.rampMissingIdsUserNegativeVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "ramps-missing")} className="btn btn-danger btn-voted">No existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addNegativeVote(road.id, "ramps-missing")} className="btn btn-danger">No existe</button></div>
                    }
                  </>
                }

              </div>
            }

            {road.blockingExists &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconBlocking} /></div>

                {road.blockingIdUserReport === localStorage.getItem("userId") ?
                  <div className="col-8"><button onClick={() => deleteMilestone(road.id, "blockings")} className="btn btn-danger btn-report">Eliminar</button></div>
                  :
                  <>
                    {
                      road.blockingIdsUserPositiveVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "blockings")} className="btn btn-success btn-voted">Existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addPositiveVote(road.id, "blockings")} className="btn btn-success">Existe</button></div>
                    }
                    {
                      road.blockingIdsUserNegativeVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "blockings")} className="btn btn-danger btn-voted">No existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addNegativeVote(road.id, "blockings")} className="btn btn-danger">No existe</button></div>
                    }
                  </>
                }

              </div>
            }

            {(road.crosswalkMissingExists && road.type === "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconCrosswalkMissing} /></div>

                {road.crosswalkMissingIdUserReport === localStorage.getItem("userId") ?
                  <div className="col-8"><button onClick={() => deleteMilestone(road.id, "crosswalks-missing")} className="btn btn-danger btn-report">Eliminar</button></div>
                  :
                  <>
                    {
                      road.crosswalkMissingIdsUserPositiveVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "crosswalks-missing")} className="btn btn-success btn-voted">Existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addPositiveVote(road.id, "crosswalks-missing")} className="btn btn-success">Existe</button></div>
                    }
                    {
                      road.crosswalkMissingIdsUserNegativeVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "crosswalks-missing")} className="btn btn-danger btn-voted">No existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addNegativeVote(road.id, "crosswalks-missing")} className="btn btn-danger">No existe</button></div>
                    }
                  </>
                }

              </div>
            }

            {(road.auditiveAlertExists && road.type === "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconAuditiveAlert} /></div>

                {road.auditiveAlertIdUserReport === localStorage.getItem("userId") ?
                  <div className="col-8"><button onClick={() => deleteMilestone(road.id, "auditive-alerts")} className="btn btn-danger btn-report">Eliminar</button></div>
                  :
                  <>
                    {
                      road.auditiveAlertIdsUserPositiveVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "auditive-alerts")} className="btn btn-success btn-voted">Existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addPositiveVote(road.id, "auditive-alerts")} className="btn btn-success">Existe</button></div>
                    }
                    {
                      road.auditiveAlertIdsUserNegativeVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "auditive-alerts")} className="btn btn-danger btn-voted">No existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addNegativeVote(road.id, "auditive-alerts")} className="btn btn-danger">No existe</button></div>
                    }
                  </>
                }

              </div>
            }

            {road.badConditionExists &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconBadCondition} /></div>

                {road.badConditionIdUserReport === localStorage.getItem("userId") ?
                  <div className="col-8"><button onClick={() => deleteMilestone(road.id, "bad-conditions")} className="btn btn-danger btn-report">Eliminar</button></div>
                  :
                  <>
                    {
                      road.badConditionIdsUserPositiveVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "bad-conditions")} className="btn btn-success btn-voted">Existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addPositiveVote(road.id, "bad-conditions")} className="btn btn-success">Existe</button></div>
                    }
                    {
                      road.badConditionIdsUserNegativeVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "bad-conditions")} className="btn btn-danger btn-voted">No existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addNegativeVote(road.id, "bad-conditions")} className="btn btn-danger">No existe</button></div>
                    }
                  </>
                }

              </div>
            }

            {(road.podotactileExists && road.type !== "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconPodotactile} /></div>

                {road.podotactileIdUserReport === localStorage.getItem("userId") ?
                  <div className="col-8"><button onClick={() => deleteMilestone(road.id, "podotactiles")} className="btn btn-danger btn-report">Eliminar</button></div>
                  :
                  <>
                    {
                      road.podotactileIdsUserPositiveVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "podotactiles")} className="btn btn-success btn-voted">Existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addPositiveVote(road.id, "podotactiles")} className="btn btn-success">Existe</button></div>
                    }
                    {
                      road.podotactileIdsUserNegativeVotes.includes(localStorage.getItem("userId")) ?
                        <div className="col-4"><button onClick={() => deleteVote(road.id, "podotactiles")} className="btn btn-danger btn-voted">No existe</button></div>
                        :
                        <div className="col-4"><button onClick={() => addNegativeVote(road.id, "podotactiles")} className="btn btn-danger">No existe</button></div>
                    }
                  </>
                }

              </div>
            }



            {road.type !== "Senda peatonal" && road.blockingExists
              && road.badConditionExists && road.podotactileExists
              ||
              road.type === "Senda peatonal" && road.rampMissingExists
              && road.blockingExists && road.crosswalkMissingExists
              && road.auditiveAlertExists && road.badConditionExists
              ? null : "No reportado para este camino"}

            {(!road.rampMissingExists && road.type === "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconRampMissing} /></div>
                <div className="col-8"><button onClick={() => addMilestone(road.id, "ramps-missing")} className="btn btn-dark btn-report">Reportar</button></div>
              </div>
            }

            {!road.blockingExists &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconBlocking} /></div>
                <div className="col-8"><button onClick={() => addMilestone(road.id, "blockings")} className="btn btn-dark btn-report">Reportar</button></div>
              </div>
            }

            {(!road.crosswalkMissingExists && road.type === "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconCrosswalkMissing} /></div>
                <div className="col-8"><button onClick={() => addMilestone(road.id, "crosswalks-missing")} className="btn btn-dark btn-report">Reportar</button></div>
              </div>
            }

            {(!road.auditiveAlertExists && road.type === "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconAuditiveAlert} /></div>
                <div className="col-8"><button onClick={() => addMilestone(road.id, "auditive-alerts")} className="btn btn-dark btn-report">Reportar</button></div>
              </div>
            }

            {!road.badConditionExists &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconBadCondition} /></div>
                <div className="col-8"><button onClick={() => addMilestone(road.id, "bad-conditions")} className="btn btn-dark btn-report">Reportar</button></div>
              </div>
            }

            {(!road.podotactileExists && road.type !== "Senda peatonal") &&
              <div className="row div-milestone">
                <div className="col-2"><img className="rounded-icon" src={iconPodotactile} /></div>
                <div className="col-8"><button onClick={() => addMilestone(road.id, "podotactiles")} className="btn btn-dark btn-report">Reportar</button></div>
              </div>
            }
          </div>
        </Popup>
      </Marker>
    )));
  }

  const addMilestone = (roadId, type) => {
    const url = graphUrl + "/" + type + "/milestone?roadId=" + roadId + "&userId=" + localStorage.getItem("userId");
    Axios.put(url, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    })
      .then((response) => {
        setData(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  const deleteMilestone = (roadId, type) => {
    const url = graphUrl + "/" + type + "/milestone?roadId=" + roadId + "&userId=" + localStorage.getItem("userId");
    Axios.delete(url, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    })
      .then((response) => {
        setData(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  const addPositiveVote = (roadId, type) => {
    const url = graphUrl + "/" + type + "/positive-vote?roadId=" + roadId + "&userId=" + localStorage.getItem("userId");
    Axios.put(url, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    })
      .then((response) => {
        setData(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  const addNegativeVote = (roadId, type) => {
    const url = graphUrl + "/" + type + "/negative-vote?roadId=" + roadId + "&userId=" + localStorage.getItem("userId");
    Axios.put(url, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    })
      .then((response) => {
        setData(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  const deleteVote = (roadId, type) => {
    const url = graphUrl + "/" + type + "/vote?roadId=" + roadId + "&userId=" + localStorage.getItem("userId");
    Axios.delete(url, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    })
      .then((response) => {
        setData(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  return (
    <div className="map-div-container">
      {map}
    </div>
  );
}

export default MapReport;