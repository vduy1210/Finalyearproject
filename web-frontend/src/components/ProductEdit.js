import React, { useState } from "react";

function ProductEdit({ product, onImageUploaded }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [localImageUrl, setLocalImageUrl] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState("");

  if (!product) {
    return <div style={{ marginTop: 20 }}>No product selected.</div>;
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

  return (
    <div style={{ border: "1px solid #2980b9", borderRadius: 8, padding: 16, marginTop: 20, background: "#22313a" }}>
      <h3>Edit Product: {product.name}</h3>
      
      {/* Product Info */}
      <div style={{ marginBottom: 16 }}>
        <strong>Price:</strong> {product.price}₫ <br />
        <strong>Stock:</strong> {product.stock}
      </div>

      {/* Image Display Section */}
      <div style={{ margin: "16px 0" }}>
        <strong>Product Image:</strong><br />
        
        {/* Show local preview if file is selected */}
        {localImageUrl && (
          <div style={{ marginTop: 8 }}>
            <strong>Selected Image Preview:</strong><br />
            <img
              src={localImageUrl}
              alt="Preview"
              style={{ 
                width: 120, 
                height: 120, 
                objectFit: "cover", 
                borderRadius: 8, 
                border: "1px solid #27ae60",
                marginTop: 4
              }}
            />
          </div>
        )}
        
        {/* Show current server image if exists */}
        {product.imageUrl && !localImageUrl && (
          <div style={{ marginTop: 8 }}>
            <strong>Current Server Image:</strong><br />
            <img
              src={`http://localhost:8081${product.imageUrl}`}
              alt={product.name}
              style={{ 
                width: 120, 
                height: 120, 
                objectFit: "cover", 
                borderRadius: 8, 
                border: "1px solid #3498db",
                marginTop: 4
              }}
            />
          </div>
        )}
        
        {/* Show message if no image */}
        {!product.imageUrl && !localImageUrl && (
          <div style={{ color: "#95a5a6", marginTop: 8 }}>
            <strong>No image selected</strong>
          </div>
        )}
      </div>

      {/* File Selection Section */}
      <div style={{ margin: "16px 0" }}>
        <strong>Select Image from Your Computer:</strong><br />
        <input 
          type="file" 
          accept="image/*" 
          onChange={handleFileChange}
          disabled={uploading}
          style={{ 
            marginTop: 8,
            padding: "8px",
            borderRadius: 4,
            border: "1px solid #3498db",
            background: "#2c3e50",
            color: "#ecf0f1"
          }}
        />
        
        <button
          onClick={handleImageUpload}
          disabled={uploading || !selectedFile}
          style={{
            marginLeft: 10,
            background: uploading ? "#95a5a6" : "#3498db",
            color: "#fff",
            border: "none",
            borderRadius: 4,
            padding: "8px 16px",
            cursor: uploading ? "not-allowed" : "pointer",
            opacity: uploading || !selectedFile ? 0.6 : 1
          }}
        >
          {uploading ? "Uploading..." : "Upload to Server"}
        </button>
      </div>

      {/* Status Messages */}
      {message && (
        <div style={{ 
          marginTop: 10, 
          color: message.includes("successfully") ? "#27ae60" : 
                 message.includes("failed") ? "#e74c3c" : "#f39c12",
          fontWeight: "bold"
        }}>
          {message}
        </div>
      )}

      {/* File Info Display */}
      {selectedFile && (
        <div style={{ 
          marginTop: 8, 
          fontSize: "12px", 
          color: "#bdc3c7",
          background: "#34495e",
          padding: "8px",
          borderRadius: 4
        }}>
          <strong>Selected File:</strong><br />
          Name: {selectedFile.name}<br />
          Size: {(selectedFile.size / 1024 / 1024).toFixed(2)} MB<br />
          Type: {selectedFile.type}<br />
          Path: {selectedFile.name} (from your computer)
        </div>
      )}
    </div>
  );
}

export default ProductEdit;