import React, { useState } from "react";
import {
  Button,
  Modal,
  TextField,
  Box,
  Typography,
  Input,
} from "@mui/material";
import { createCheese } from "../api/cheese";
import { styled } from "@mui/material/styles";

const CreateCheeseButton = styled(Button)(({ theme }) => ({
  backgroundColor: "#ffa600",
  color: theme.palette.getContrastText("#ffa600"),
  "&:hover": {
    backgroundColor: "#e69500",
  },
}));

function CreateCheese({ onCheeseCreated }) {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [color, setColor] = useState("");
  const [price, setPrice] = useState("");
  const [imageFile, setImageFile] = useState(null);
  const [error, setError] = useState("");

  const handleOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
    resetForm();
  };

  const resetForm = () => {
    setName("");
    setColor("");
    setPrice("");
    setImageFile(null);
    setError("");
  };

  const handleImageChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setImageFile(e.target.files[0]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!name || !color || !price || !imageFile) {
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
      formData.append("imageFile", imageFile);

      const cheeseData = JSON.stringify({
        name,
        color,
        price: floatPrice,
      });
      formData.append(
        "cheese",
        new Blob([cheeseData], { type: "application/json" })
      );

      const newCheese = await createCheese(formData);
      onCheeseCreated(newCheese);
      handleClose();
      window.location.reload();
    } catch (error) {
      console.error("Failed to create cheese:", error);
      setError("Failed to create cheese. Please try again.");
    }
  };

  return (
    <>
      <CreateCheeseButton variant="contained" onClick={handleOpen}>
        Create Cheese
      </CreateCheeseButton>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="create-cheese-modal"
      >
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 400,
            bgcolor: "background.paper",
            boxShadow: 24,
            p: 4,
            borderRadius: 2,
          }}
        >
          <Typography variant="h6" component="h2" gutterBottom>
            Create Cheese
          </Typography>
          <form onSubmit={handleSubmit}>
            <Input
              type="file"
              onChange={handleImageChange}
              fullWidth
              required
              inputProps={{ accept: "image/*" }}
            />
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
            <Box sx={{ mt: 2, display: "flex", justifyContent: "flex-end" }}>
              <Button onClick={handleClose} sx={{ mr: 1 }}>
                Cancel
              </Button>
              <Button type="submit" variant="contained" color="primary">
                Create
              </Button>
            </Box>
          </form>
        </Box>
      </Modal>
    </>
  );
}

export default CreateCheese;
