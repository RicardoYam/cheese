import React, { useState, useEffect } from "react";
import { fetchAllCheeses } from "../api/cheese";
import CheeseCard from "../components/CheeseCard";
import { Typography, Container, Grid, Box } from "@mui/material";
import CreateCheese from "../components/CreateCheese";
import Calculator from "../components/Calculator";

function Cheese() {
  const [cheeses, setCheeses] = useState([]);

  useEffect(() => {
    fetchAllCheeses().then(setCheeses);
  }, []);

  const handleCheeseCreated = (newCheese) => {
    setCheeses((prevCheeses) => [...prevCheeses, newCheese]);
  };

  const handleCheeseUpdated = (updatedCheese) => {
    setCheeses((prevCheeses) =>
      prevCheeses.map((cheese) =>
        cheese.id === updatedCheese.id ? updatedCheese : cheese
      )
    );
  };

  return (
    <Container maxWidth="lg" className="py-8">
      <Typography
        variant="h3"
        component="h1"
        className="font-bold text-yellow-600 text-center mb-8"
      >
        🧀Cheese🧀
      </Typography>
      <Box className="flex justify-between mb-8">
        <Calculator />
        <CreateCheese onCheeseCreated={handleCheeseCreated} />
      </Box>
      <Grid container spacing={4}>
        {cheeses.map((cheese) => (
          <Grid item key={cheese.id} xs={12} sm={6} md={4} lg={3}>
            <CheeseCard cheese={cheese} onCheeseUpdated={handleCheeseUpdated} />
          </Grid>
        ))}
      </Grid>
    </Container>
  );
}

export default Cheese;
