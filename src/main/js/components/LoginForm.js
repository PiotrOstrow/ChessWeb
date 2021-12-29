import Api from "../api/Api";
import React, {useEffect, useState} from "react";
import {Box, Button, FormHelperText, Grid, Link, TextField, Typography} from "@mui/material";

function LoginForm(props) {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState(null);
    const [loggingIn, setLoggingIn] = useState(true);

    useEffect(() => setErrorMessage(null), [loggingIn]);

    const onSubmit = event => {
        event.preventDefault();

        setErrorMessage(null);

        if (loggingIn) {
            Api.login(username, password)
                .then(response => props.onLoggedIn())
                .catch(handleError);
        } else {
            Api.register(username, password, email)
                .then(response => {
                    setLoggingIn(true);
                    alert("Signed up!")
                }).catch(handleError);
        }
    }

    const handleError = error => {
        if (error.response) {
            setErrorMessage(error.response.data.message);
        } else {
            setErrorMessage('Error connecting to the server: ' + error.message);
        }
    };

    return (
        <Box sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
        }}>

        <Typography component="h1" variant="h5" sx={{mt: 1}}>
                {loggingIn ? 'Sign in' : 'Sign up'}
            </Typography>

            <Box component="form" onSubmit={onSubmit} sx={{mt: 3, width: '460px'}}>
                <TextField
                    required
                    fullWidth
                    variant="outlined"
                    label="Username"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    margin="normal"
                    id="margin-normal"/>

                {!loggingIn &&
                <TextField
                    required
                    fullWidth
                    variant="outlined"
                    label="E-mail"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    margin="normal"
                    id="email"/>
                }

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
                    {loggingIn ? 'Login' : 'Sign up'}
                </Button>
                {loggingIn &&
                <Grid container>
                    <Grid item xs>
                        <Link href="#" variant="body2">
                            Forgot password?
                        </Link>
                    </Grid>
                    <Grid item>
                        <Link href="#" variant="body2" onClick={() => setLoggingIn(false)}>
                            {"Don't have an account? Sign Up"}
                        </Link>
                    </Grid>
                </Grid>
                }
                {!loggingIn &&
                <Grid container justifyContent="flex-end">
                    <Grid item>
                        <Link href="#" variant="body2" onClick={() => setLoggingIn(true)}>
                            {"Already have an account? Sign in"}
                        </Link>
                    </Grid>
                </Grid>
                }
            </Box>
        </Box>
    )
}

export default LoginForm;