import React from "react";
import {useNavigate} from "react-router-dom";
import {
    AppBar,
    Avatar,
    Box,
    Button,
    Container,
    IconButton,
    Menu,
    MenuItem,
    Toolbar,
    Tooltip,
    Typography
} from "@mui/material";

// https://mui.com/components/app-bar/
function NavBar() {
    const [anchorElUser, setAnchorElUser] = React.useState(null);
    const navigate = useNavigate();

    const pages = [
        {name: 'Play', path: '/'},
        {name: 'Profile', path: '/profile'}];

    const settings = [
        {name: 'Profile', path: '/profile'},
        {name: 'Account', path: '/'},
        {name: 'Logout', path: '/'}];


    const handleOpenUserMenu = (event) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    const handleCloseNavMenu = (path) => {
        navigate(path);
        setAnchorElUser(null);
    };

    return (
        <AppBar position="static" style={{margin: '10px auto', width: '90%', maxWidth: '1200px'}}>
            <Container>
                <Toolbar disableGutters>
                    <Typography
                        variant="h6"
                        noWrap
                        component="div"
                        sx={{mr: 2, display: 'flex'}}
                    >
                        Chess
                    </Typography>
                    <Box sx={{flexGrow: 1, display: 'flex', justifyContent: 'center'}}>
                        {pages.map((page) => (
                            <Button
                                key={page.name}
                                onClick={() => handleCloseNavMenu(page.path)}
                                sx={{my: 2, color: 'white', display: 'block'}}
                            >
                                {page.name}
                            </Button>
                        ))}
                    </Box>

                    <Box sx={{flexGrow: 0}}>
                        <Tooltip title="Open settings">
                            <IconButton onClick={handleOpenUserMenu} sx={{p: 0}}>
                                <Avatar alt="P" src="1.jpg"/>
                            </IconButton>
                        </Tooltip>
                        <Menu
                            sx={{mt: '45px'}}
                            id="menu-appbar"
                            anchorEl={anchorElUser}
                            anchorOrigin={{vertical: 'top', horizontal: 'right'}}
                            keepMounted
                            transformOrigin={{vertical: 'top', horizontal: 'right'}}
                            open={Boolean(anchorElUser)}
                            onClose={handleCloseUserMenu}
                        >
                            {settings.map((setting) => (
                                <MenuItem key={setting.name} onClick={() => handleCloseNavMenu(setting.path)}>
                                    <Typography textAlign="center">{setting.name}</Typography>
                                </MenuItem>
                            ))}
                        </Menu>
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}

export default NavBar;
