import React from "react";
import { Navigate, Outlet } from "react-router-dom";

function Gateway({ tokenIsExpected, redirectTo = "/login" }) {
  let deny;
  if (tokenIsExpected) {
    deny = !localStorage.getItem("token")
  } else {
    deny = !!localStorage.getItem("token")
  }

  if (deny) {
    return <Navigate to={redirectTo} />
  }
  return (<Outlet />);
}

export default Gateway;