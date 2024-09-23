/* Styling for the label */
label {
  display: block;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

/* Styling for the select (drop-down) menus */
select {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  margin-top: 5px;
}

/* Add hover effect on select */
select:hover {
  border-color: #888;
  background-color: #fff;
}

/* Add focus effect on select */
select:focus {
  border-color: #0056b3;
  outline: none;
  box-shadow: 0 0 5px rgba(0, 86, 179, 0.5);
}

/* Styling for the entire form container (optional) */
form {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f4f4f4;
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

/* Responsive design: for smaller screens */
@media (max-width: 600px) {
  select {
    width: 100%;
    font-size: 14px;
  }

  label {
    font-size: 14px;
  }
}
