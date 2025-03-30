import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 5,               // 동시에 요청할 가상 유저 수
    duration: '2s',        // 테스트 시간 (원하면 조절 가능)
};

export default function () {
    const url = 'http://localhost:8080/api/v1/movies/reservations';

    const payload = JSON.stringify({
        screeningId: 1,
        userId: `user-${__VU}`,      // 각 VU마다 유니크한 userId 부여
        seatIds: [2]                 // 모두 동일한 좌석 ID 요청 (동시성 충돌 유도)
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.post(url, payload, params);

    sleep(1);
}
