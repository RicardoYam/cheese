import React, { useState, useEffect } from "react";
import {
  Select,
  MenuItem,
  TextField,
  Button,
  Typography,
  Box,
  Modal,
} from "@mui/material";
import { fetchAllCheeses } from "../api/cheese";

function Calculator() {
  const [open, setOpen] = useState(false);
  const [cheeses, setCheeses] = useState([]);
  const [selectedCheese, setSelectedCheese] = useState("");
  const [weight, setWeight] = useState("");
  const [total, setTotal] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchAllCheeses().then(setCheeses);
  }, []);

  const handleOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
    resetForm();
  };

  const resetForm = () => {
    setSelectedCheese("");
    setWeight("");
    setTotal(null);
    setError("");
  };

  const handleCheeseChange = (event) => {
    setSelectedCheese(event.target.value);
  };

  const handleWeightChange = (event) => {
    setWeight(event.target.value);
  };

  const calculateTotal = () => {
    setError("");

    if (!selectedCheese || !weight) {
      setError("Please select a cheese and enter a weight");
      return;
    }

    const cheese = cheeses.find((c) => c.id === selectedCheese);
    const floatWeight = parseFloat(weight);

    if (isNaN(floatWeight) || floatWeight <= 0) {
      setError("Please enter a valid weight");
      return;
    }

    const totalPrice = cheese.price * floatWeight;
    setTotal(totalPrice.toFixed(2));
  };

  return (
    <>
      <Button variant="contained" onClick={handleOpen} color="primary">
        Calculate Price
      </Button>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="calculator-modal"
      >
        <Box className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white p-6 rounded-lg shadow-xl w-11/12 max-w-md">
          <Typography variant="h5" component="h2" className="mb-4 font-bold">
            Cheese Price Calculator
          </Typography>
          <Select
            value={selectedCheese}
            onChange={handleCheeseChange}
            fullWidth
            displayEmpty
            className="mb-4"
          >
            <MenuItem value="" disabled>
              Select a cheese
            </MenuItem>
            {cheeses.map((cheese) => (
              <MenuItem key={cheese.id} value={cheese.id}>
                {cheese.name} - ${cheese.price}/kg
              </MenuItem>
            ))}
          </Select>
          <TextField
            label="Weight (kg)"
            type="number"
            value={weight}
            onChange={handleWeightChange}
            fullWidth
            className="mb-4"
          />
          <Button
            variant="contained"
            onClick={calculateTotal}
            fullWidth
            className="mb-4"
            color="primary"
          >
            Calculate
          </Button>
          {error && (
            <Typography color="error" variant="body2" className="mb-2">
              {error}
            </Typography>
          )}
          {total !== null && (
            <Typography variant="h6" align="center" className="mb-4">
              Total: ${total}
            </Typography>
          )}
          <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
            <Button onClick={handleClose}>Close</Button>
          </Box>
        </Box>
      </Modal>
    </>
  );
}

export default Calculator;
