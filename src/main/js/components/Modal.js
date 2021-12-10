import React from "react";

function Modal(props) {
    return (
        <div className="modal-container">
            <div className="modal">
                {props.children}
            </div>
        </div>
    );
}

export default Modal;
