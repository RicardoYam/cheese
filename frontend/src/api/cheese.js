import API from "./base";

export const fetchAllCheeses = async () => {
  try {
    const response = await API.get("/cheeses");
    if (response.status === 200) {
      return response.data;
    }
    return [];
  } catch (error) {
    console.error("Failed to fetch cheeses:", error);
    throw error;
  }
};

export const fetchCheeseById = async (id) => {
  try {
    const response = await API.get(`/cheeses/${id}`);
    if (response.status === 200) {
      return response.data;
    }
    return null;
  } catch (error) {
    console.error("Failed to fetch cheese by id:", error);
    throw error;
  }
};

export const createCheese = async (formData) => {
  try {
    const response = await API.post("/cheeses", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Failed to create cheese:", error);
    throw error;
  }
};

export const updateCheese = async (id, formData) => {
  try {
    const response = await API.put(`/cheeses/${id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Failed to update cheese:", error);
    throw error;
  }
};

export const deleteCheese = async (id) => {
  try {
    const response = await API.delete(`/cheeses/${id}`);
    return response.data;
  } catch (error) {
    console.error("Failed to delete cheese:", error);
    throw error;
  }
};
