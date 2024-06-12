import { useDispatch } from "react-redux";
import { logout } from "../Store/Action/AuthAction";
import { useNavigate } from "react-router-dom";
export const getInterestRate = (age) => {
  if (age < 18) return 6;
  else if (age >= 18 && age < 60) return 12;
  else return 8;
};

export const Logout = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  sessionStorage.clear();
  dispatch(logout());
  navigate("/");
};
