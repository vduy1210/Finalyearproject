import React, { useState } from "react";

function ProductEdit({ product, onImageUploaded }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [localImageUrl, setLocalImageUrl] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState("");

  if (!product) {
    return <div style={{ marginTop: 20, color: '#888', fontFamily: 'Roboto, Arial, sans-serif' }}>No product selected.</div>;
  }

  function handleFileChange(e) {
    const file = e.target.files[0];
    setSelectedFile(file);
    setMessage("");

    if (file) {
      // Create a local URL for preview
      const url = URL.createObjectURL(file);
      setLocalImageUrl(url);
    } else {
      setLocalImageUrl(null);
    }
  }

  function handleImageUpload() {
    if (!selectedFile) {
      setMessage("Please select an image file first.");
      return;
    }

    setUploading(true);
    const formData = new FormData();
    formData.append("file", selectedFile);

    fetch(`http://localhost:8081/api/products/${product.id}/image`, {
      method: "POST",
      body: formData,
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        setMessage("Image uploaded successfully!");
        setUploading(false);
        setSelectedFile(null);
        setLocalImageUrl(null);
        // Clear the file input
        const fileInput = document.querySelector('input[type="file"]');
        if (fileInput) fileInput.value = '';
        // Refresh product list to show new image
        if (onImageUploaded) onImageUploaded();
      })
      .catch(error => {
        console.error('Upload failed:', error);
        setMessage(`Upload failed: ${error.message}`);
        setUploading(false);
      });
  }

  // Style đồng bộ với ProductManagerPanel/Menu/Cart
  const panelStyle = {
    border: "1.5px solid #bbdefb",
    borderRadius: 16,
    padding: 24,
    marginTop: 28,
    background: "#fff",
    boxShadow: "0 4px 24px rgba(44,62,80,0.08)",
    maxWidth: 500,
    marginLeft: "auto",
    marginRight: "auto",
    fontFamily: 'Roboto, Arial, sans-serif',
    marginBottom: 32
  };
  const titleStyle = {
    color: "#1976d2",
    fontWeight: 800,
    fontSize: 20,
    marginBottom: 18,
    letterSpacing: 1,
    textAlign: "center"
  };
  const infoStyle = {
    marginBottom: 18,
    color: "#1976d2",
    fontWeight: 500,
    fontSize: 16
  };
  const imageBox = {
    margin: "18px 0",
    textAlign: "center"
  };
  const imageStyle = {
    width: 120,
    height: 120,
    objectFit: "cover",
    borderRadius: 10,
    border: "2px solid #bbdefb",
    background: "#e3f2fd",
    boxShadow: "0 2px 8px rgba(52,152,219,0.08)",
    marginTop: 8
  };
  const placeholderStyle = {
    ...imageStyle,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#90a4ae",
    fontSize: 15,
    fontStyle: "italic",
    border: "2px dashed #bbdefb"
  };
  const fileInputStyle = {
    marginTop: 8,
    padding: "8px 0",
    borderRadius: 8,
    border: "1.5px solid #bbdefb",
    background: "#f5f7fa",
    color: "#1976d2",
    fontWeight: 500,
    width: "100%"
  };
  const buttonStyle = {
    marginLeft: 0,
    marginTop: 14,
    background: uploading ? "#90caf9" : "#1976d2",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    padding: "12px 0",
    cursor: uploading ? "not-allowed" : "pointer",
    opacity: uploading ? 0.7 : 1,
    fontWeight: 700,
    fontSize: 16,
    width: "100%",
    boxShadow: "0 2px 8px rgba(25,118,210,0.10)",
    transition: "background 0.18s"
  };
  const msgStyle = {
    marginTop: 12,
    color: message.includes("successfully") ? "#27ae60" : message.includes("failed") ? "#e74c3c" : "#f39c12",
    fontWeight: "bold",
    textAlign: "center"
  };
  const fileInfoStyle = {
    marginTop: 10,
    fontSize: 13,
    color: "#1976d2",
    background: "#e3f2fd",
    padding: "8px 12px",
    borderRadius: 8,
    textAlign: "left"
  };

  return (
    <div style={panelStyle}>
      <div style={titleStyle}>Edit Product: {product.name}</div>
      {/* Product Info */}
      <div style={infoStyle}>
        <span>Price: <b>{product.price.toLocaleString()}₫</b></span> &nbsp; | &nbsp;
        <span>Stock: <b>{product.stock}</b></span>
      </div>
      {/* Image Display Section */}
      <div style={imageBox}>
        <div style={{ color: "#1976d2", fontWeight: 600, marginBottom: 6 }}>Product Image</div>
        {/* Show local preview if file is selected */}
        {localImageUrl && (
          <div>
            <div style={{ color: "#64b5f6", fontWeight: 500, marginBottom: 4 }}>Selected Image Preview</div>
            <img src={localImageUrl} alt="Preview" style={imageStyle} />
          </div>
        )}
        {/* Show current server image if exists */}
        {product.imageUrl && !localImageUrl && (
          <div>
            <div style={{ color: "#64b5f6", fontWeight: 500, marginBottom: 4 }}>Current Server Image</div>
            <img src={`http://localhost:8081${product.imageUrl}`} alt={product.name} style={imageStyle} />
          </div>
        )}
        {/* Show message if no image */}
        {!product.imageUrl && !localImageUrl && (
          <div style={placeholderStyle}>No image selected</div>
        )}
      </div>
      {/* File Selection Section */}
      <div style={{ margin: "18px 0" }}>
        <div style={{ color: "#1976d2", fontWeight: 600, marginBottom: 6 }}>Select Image from Your Computer</div>
        <input
          type="file"
          accept="image/*"
          onChange={handleFileChange}
          disabled={uploading}
          style={fileInputStyle}
        />
        <button
          onClick={handleImageUpload}
          disabled={uploading || !selectedFile}
          style={buttonStyle}
        >
          {uploading ? "Uploading..." : "Upload to Server"}
        </button>
      </div>
      {/* Status Messages */}
      {message && (
        <div style={msgStyle}>{message}</div>
      )}
      {/* File Info Display */}
      {selectedFile && (
        <div style={fileInfoStyle}>
          <strong>Selected File:</strong><br />
          Name: {selectedFile.name}<br />
          Size: {(selectedFile.size / 1024 / 1024).toFixed(2)} MB<br />
          Type: {selectedFile.type}<br />
        </div>
      )}
    </div>
  );
}

export default ProductEdit;