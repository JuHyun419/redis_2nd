import http from 'k6/http';
import { sleep } from "k6";

// https://grafana.com/docs/k6/latest/get-started/running-k6/
export const options = {
    vus: 100,
    duration: '10s'
};

export default function () {
    http.get("http://localhost:8080/api/v1/movies?title=Inception&genre=SF");

    sleep(1);
}
