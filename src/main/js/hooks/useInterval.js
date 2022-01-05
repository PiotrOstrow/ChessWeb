import {useEffect, useRef} from "react";

export default function useInterval(callback, interval) {
    const savedCallback = useRef();

    useEffect(() => {
        savedCallback.current = callback;
    }, [callback]);

    useEffect(() => {
        if (interval !== null) {
            let id = setInterval(() => savedCallback.current(), interval);
            return () => clearInterval(id);
        }
    }, [interval]);
}
