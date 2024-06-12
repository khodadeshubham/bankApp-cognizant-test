import React, { useEffect, useState } from "react";
import { BASE_URL } from "../../Constants/Constant";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import "../Overview/Overview.css";
import CircularProgress from "@mui/material/CircularProgress";
import { useNavigate } from "react-router-dom";
import { Logout } from "../../Util/Util";
import { logout } from "../../Store/Action/AuthAction";

function Overview() {
  const userDetails = useSelector((state) => state.auth.authDetails);
  const [accountDetails, setAccountDetails] = useState();
  const [loader, setLoader] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    if (userDetails !== null) {
      setLoader(true);
      const URL = `${BASE_URL}/user/userBankDetails/${userDetails.user.user_Id}`;
      axios
        .get(URL)
        .then((resp) => {
          if (resp.status !== 200) {
            throw new Error(resp);
          }
          setAccountDetails(resp.data);
          setLoader(false);
        })
        .catch((err) => {
          console.log(err);
          if (err.response.status == 401) {
            sessionStorage.clear();
            dispatch(logout());
          } else {
            setAccountDetails();
            setLoader(false);
          }
        });
    }
  }, []);

  return (
    <>
      {loader && <CircularProgress />}
      {!loader && accountDetails && (
        <div className="overview-details">
          <h3>Account Overview</h3>
          <div>
            <h4>Account Holder Name</h4>
            <p>{accountDetails.name}</p>
          </div>
          <div>
            <h4>Branch</h4>
            <p>{accountDetails.bankList[0].branch}</p>
          </div>
          <div>
            <h4>{accountDetails.bankList[0].accountType + " Account"}</h4>
            <p>{accountDetails.bankList[0].accountNumber}</p>
          </div>
          <div>
            <h4>Balance</h4>
            <p>{accountDetails.bankList[0].balance}</p>
          </div>
        </div>
      )}
    </>
  );
}

export default Overview;
