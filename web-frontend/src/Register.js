import { TextField, Button, Typography, Paper, Box } from "@mui/material";

// Register.js
function Register() {
  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
      <Paper elevation={3} sx={{ p: 4, width: 350 }}>
        <Typography variant="h5" fontWeight="bold" mb={2}>Register</Typography>
        <form>
          <TextField type="text" label="Name" variant="outlined" fullWidth margin="normal" required />
          <TextField type="email" label="Email" variant="outlined" fullWidth margin="normal" required />
          <TextField type="password" label="Password" variant="outlined" fullWidth margin="normal" required />
          <TextField type="password" label="Confirm Password" variant="outlined" fullWidth margin="normal" required />
          <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
            Register
          </Button>
        </form>
      </Paper>
    </Box>
  );
}

export default Register;
