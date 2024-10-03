import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { databaseName, dbTableName, db_column_name, row } = location.state || {};

  const [formData, setFormData] = useState({
    db_column_name: db_column_name || '',
    file_column_name: row?.file_column_name || '',
    file_name: row?.file_name || '',
    file_source: row?.file_source || '',
  });

  const handleSubmit = () => {
    // Prepare data for submission
    const submitData = {
      databaseName,
      dbTableName,
      db_column_name: formData.db_column_name,
      file_column_name: formData.file_column_name,
      file_name: formData.file_name,
      file_source: formData.file_source,
    };

    // Send the updated data to the server
    fetch(`http://localhost:8080/updateData`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(submitData),
    })
      .then((response) => response.json())
      .then((result) => {
        console.log('Data updated successfully:', result);
        navigate(-1); // Navigate back after submission
      })
      .catch((error) => console.error('Error updating data:', error));
  };

  const handleCancel = () => {
    navigate(-1); // Go back without saving
  };

  return (
    <div className="root">
      <div className="main">
        <div id="holder">
          <div id="content-top">
            <div id="bannerContentSmall">
              <div className="header">
                <div className="headerLeft"></div>
              </div>
            </div>
          </div>
          <div className="content-bottom">
            <div className="top-most-div">
              <div className="breadcrumb">
                <span className="breadcrumbLeftInside">
                  <b>Edit Data Screen</b>
                </span>
              </div>
              <div className="highlight">
                <table className="headTable">
                  <tbody>
                    <tr>
                      <th>Db Column Name</th>
                      <th>File Column Name</th>
                      <th>File Name</th>
                      <th>File Source</th>
                    </tr>
                    <tr>
                      <td>
                        <input
                          type="text"
                          value={formData.db_column_name}
                          onChange={(e) =>
                            setFormData({
                              ...formData,
                              db_column_name: e.target.value,
                            })
                          }
                          disabled // Disable editing of db_column_name
                        />
                      </td>
                      <td>
                        <input
                          type="text"
                          value={formData.file_column_name}
                          onChange={(e) =>
                            setFormData({
                              ...formData,
                              file_column_name: e.target.value,
                            })
                          }
                        />
                      </td>
                      <td>
                        <input
                          type="text"
                          value={formData.file_name}
                          onChange={(e) =>
                            setFormData({
                              ...formData,
                              file_name: e.target.value,
                            })
                          }
                        />
                      </td>
                      <td>
                        <input
                          type="text"
                          value={formData.file_source}
                          onChange={(e) =>
                            setFormData({
                              ...formData,
                              file_source: e.target.value,
                            })
                          }
                        />
                      </td>
                    </tr>
                  </tbody>
                </table>
                <table className="table-xml">
                  <tbody>
                    <tr>
                      <td>
                        <button onClick={handleSubmit} className="btn submit-btn">
                          Submit
                        </button>
                        <button onClick={handleCancel} className="btn cancel-btn">
                          Cancel
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddEditDataScreen;
