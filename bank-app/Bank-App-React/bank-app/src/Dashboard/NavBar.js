import React from "react";
import "../Dashboard/Dashboard.css";
import { useDispatch } from "react-redux";
import { logout } from "../Store/Action/AuthAction";
import { useNavigate } from "react-router-dom";
import axios from "axios";
function Navbar(props) {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  function handleLogout() {
    sessionStorage.clear();
    axios.interceptors.request.use(
      (config) => {
        config.headers["Authorization"] = "";
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );
    dispatch(logout());
    navigate("/");
    window.location.reload();
  }
  return (
    <div style={{ width: "10%" }}>
      <nav className="navbar">
        <ul className="nav-list">
          <li
            onClick={() => props.onOptionChange("overview")}
            className={
              props.selectedOption === "overview"
                ? "active listItem"
                : "listItem"
            }
          >
            Account Overview
          </li>
          <li
            onClick={() => props.onOptionChange("deposit")}
            className={
              props.selectedOption === "deposit"
                ? "active listItem"
                : "listItem"
            }
          >
            Deposit Money
          </li>
          <li
            onClick={() => props.onOptionChange("loanApply")}
            className={
              props.selectedOption === "loanApply"
                ? "active listItem"
                : "listItem"
            }
          >
            Apply Loan
          </li>
          <li
            onClick={() => props.onOptionChange("loanDetails")}
            className={
              props.selectedOption === "loanDetails"
                ? "active listItem"
                : "listItem"
            }
          >
            View Loan Details
          </li>
          <li
            onClick={() => handleLogout()}
            className={
              props.selectedOption === "logout" ? "active listItem" : "listItem"
            }
          >
            Logout
          </li>
        </ul>
      </nav>
    </div>
  );
}

export default Navbar;
