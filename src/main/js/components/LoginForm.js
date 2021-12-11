import Api from "../api/Api";
import React, {useState} from "react";
import {Box, Button, CssBaseline, FormHelperText, Grid, Link, TextField, Typography} from "@mui/material";

function LoginForm(props) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState(null);

    const onSubmit = event => {
        event.preventDefault();

        setErrorMessage(null);

        Api.login(username, password)
            .then(response => props.onLoggedIn())
            .catch(error => {
                if(error.response) {
                    switch (error.response.status) {
                        case 401: setErrorMessage('Incorrect username or password'); break;
                        default: setErrorMessage('Unknown error: ' + error.message);
                    }
                } else {
                    setErrorMessage('Error connecting to the server: ' + error.message);
                }
            });
    }

    return (
        <Box sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
        }}>
            <CssBaseline/>
            <Typography component="h1" variant="h5" sx={{mt: 1}}>
                Sign in
            </Typography>

            <Box component="form" onSubmit={onSubmit} sx={{mt: 1}}>
                <TextField
                    required
                    fullWidth
                    variant="outlined"
                    label="Username"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    margin="normal"
                    id="margin-normal" />

                <TextField
                    required
                    fullWidth
                    variant="outlined"
                    label="Password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    margin="normal"
                    id="margin-normal"
                    type="password" />

                <FormHelperText error={true} >{errorMessage}</FormHelperText>

                <Button
                    variant="contained"
                    type="submit"
                    fullWidth
                    sx={{mt: 3, mb: 3}}
                >
                    Login
                </Button>
                <Grid container>
                    <Grid item xs>
                        <Link href="#" variant="body2">
                            Forgot password?
                        </Link>
                    </Grid>
                    <Grid item>
                        <Link href="#" variant="body2">
                            {"Don't have an account? Sign Up"}
                        </Link>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    )
}

export default LoginForm;