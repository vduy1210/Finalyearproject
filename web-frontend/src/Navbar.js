import React from "react";
import { Link, useNavigate } from "react-router-dom";

function Navbar({ userName, setUserName }) {
  const navigate = useNavigate();

  function handleLogout() {
    localStorage.removeItem("userName");
    setUserName("");
    navigate("/login");
  }

  return (
    <nav style={{ background: "#3498db", padding: "12px 0", marginBottom: 20 }}>
      <ul style={{
        display: "flex",
        listStyle: "none",
        justifyContent: "center",
        alignItems: "center",
        margin: 0,
        padding: 0,
        gap: 20
      }}>
        <li>
          <Link to="/menu" style={{ color: "#fff", textDecoration: "none", fontWeight: 500 }}>Menu</Link>
        </li>
        <li>
          <Link to="/cart" style={{ color: "#fff", textDecoration: "none", fontWeight: 500 }}>Cart</Link>
        </li>
        <li>
          <Link to="/orders" style={{ color: "#fff", textDecoration: "none", fontWeight: 500 }}>Orders</Link>
        </li>
        <li>
          <Link to="/product-manager" style={{ color: "#fff", textDecoration: "none", fontWeight: 500 }}>Product Manager</Link>
        </li>
        <li style={{ marginLeft: 30 }}>
          {userName ? (
            <>
              <span style={{ color: "#fff", fontWeight: "bold", marginRight: 12 }}>
                Hello, {userName}
              </span>
              <button
                onClick={handleLogout}
                style={{
                  background: "#e74c3c",
                  color: "#fff",
                  border: "none",
                  borderRadius: 4,
                  padding: "6px 14px",
                  cursor: "pointer"
                }}
              >
                Logout
              </button>
            </>
          ) : (
            <Link to="/login" style={{ color: "#fff", textDecoration: "none", fontWeight: 500 }}>
              Login
            </Link>
          )}
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;
