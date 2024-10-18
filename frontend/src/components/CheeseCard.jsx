import React, { useState, useRef } from "react";
import {
  Card,
  CardContent,
  Typography,
  CardMedia,
  Button,
  Modal,
  Box,
  TextField,
  IconButton,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import { fetchCheeseById, deleteCheese, updateCheese } from "../api/cheese";

function CheeseCard({ cheese, onCheeseUpdated }) {
  const [open, setOpen] = useState(false);
  const [cheeseDetails, setCheeseDetails] = useState(null);
  const [update, setUpdate] = useState(false);
  const [name, setName] = useState("");
  const [color, setColor] = useState("");
  const [price, setPrice] = useState("");
  const [newImage, setNewImage] = useState(null);
  const [error, setError] = useState("");
  const fileInputRef = useRef(null);

  const handleOpen = async () => {
    const details = await fetchCheeseById(cheese.id);
    setCheeseDetails(details);
    setName(details.name);
    setColor(details.color);
    setPrice(details.price);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setUpdate(false);
    setError("");
    setNewImage(null);
  };

  const handleDelete = async () => {
    await deleteCheese(cheese.id);
    window.location.reload();
  };

  const handleImageEdit = () => {
    fileInputRef.current.click();
  };

  const handleImageChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setNewImage(e.target.files[0]);
    }
  };

  const handleUpdate = async () => {
    setError("");

    if (!name || !color || !price) {
      setError("All fields are required");
      return;
    }

    const floatPrice = parseFloat(price);
    if (isNaN(floatPrice)) {
      setError("Price must be a valid number");
      return;
    }

    try {
      const formData = new FormData();
      if (newImage) {
        formData.append("imageFile", newImage);
      }

      const cheeseData = JSON.stringify({
        name,
        color,
        price: floatPrice,
      });
      formData.append(
        "cheese",
        new Blob([cheeseData], { type: "application/json" })
      );

      const updatedCheese = await updateCheese(cheese.id, formData);
      setCheeseDetails(updatedCheese);
      setUpdate(false);
      onCheeseUpdated(updatedCheese);
      handleClose();
      window.location.reload();
    } catch (error) {
      console.error("Failed to update cheese:", error);
      setError("Failed to update cheese. Please try again.");
    }
  };

  return (
    <>
      <Card className="h-full shadow-md hover:shadow-lg hover:scale-105 transition-shadow ease-in-out duration-300">
        <CardMedia
          component="img"
          height="140"
          image={cheese.imageData}
          alt={cheese.name}
          className="h-36 object-cover"
        />
        <CardContent className="flex flex-col h-full">
          <Typography variant="h6" component="h3">
            {cheese.name}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Color: <span className="font-medium">{cheese.color}</span>
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Price:{" "}
            <span className="font-medium">{cheese.price} / Kilogram</span>
          </Typography>
          <Button variant="contained" color="primary" onClick={handleOpen}>
            View
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleDelete}
            className="mt-2"
          >
            Delete
          </Button>
        </CardContent>
      </Card>

      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="cheese-detail-modal"
      >
        <Box className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white p-6 rounded-lg shadow-xl w-11/12 max-w-md">
          {cheeseDetails && (
            <div className="relative">
              <Typography
                variant="h5"
                component="h2"
                className="mb-4 font-bold"
              >
                {update ? "Update Cheese" : cheeseDetails.name}
              </Typography>
              <div className="relative">
                <img
                  src={
                    newImage
                      ? URL.createObjectURL(newImage)
                      : cheeseDetails.imageData
                  }
                  alt={cheeseDetails.name}
                  className="w-full h-48 object-cover rounded-md mb-4"
                />
                {update ? (
                  <>
                    <IconButton className="bg-white" onClick={handleImageEdit}>
                      <EditIcon />
                    </IconButton>
                    <input
                      type="file"
                      ref={fileInputRef}
                      onChange={handleImageChange}
                      style={{ display: "none" }}
                      accept="image/*"
                    />
                  </>
                ) : null}
              </div>
              {update ? (
                <>
                  <TextField
                    fullWidth
                    label="Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    margin="normal"
                    required
                  />
                  <TextField
                    fullWidth
                    label="Color"
                    value={color}
                    onChange={(e) => setColor(e.target.value)}
                    margin="normal"
                    required
                  />
                  <TextField
                    fullWidth
                    label="Price"
                    type="number"
                    inputProps={{ step: "0.01" }}
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                    margin="normal"
                    required
                  />
                  {error && (
                    <Typography color="error" variant="body2" gutterBottom>
                      {error}
                    </Typography>
                  )}
                </>
              ) : (
                <>
                  <Typography variant="body1" className="mb-2">
                    Color: {cheeseDetails.color}
                  </Typography>
                  <Typography variant="body1" className="mb-2">
                    Price: {cheeseDetails.price} / Kilogram
                  </Typography>
                </>
              )}
              <Box sx={{ mt: 2, display: "flex", justifyContent: "flex-end" }}>
                {update ? (
                  <>
                    <Button onClick={() => setUpdate(false)} sx={{ mr: 1 }}>
                      Cancel
                    </Button>
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={handleUpdate}
                    >
                      Save
                    </Button>
                  </>
                ) : (
                  <>
                    <Button onClick={handleClose} sx={{ mr: 1 }}>
                      Close
                    </Button>
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={() => setUpdate(true)}
                    >
                      Update
                    </Button>
                  </>
                )}
              </Box>
            </div>
          )}
        </Box>
      </Modal>
    </>
  );
}

export default CheeseCard;
