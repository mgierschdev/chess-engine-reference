import {ChessGame} from "@/app/_private/model/ChessGame";
import {ChessMove} from "@/app/_private/model/ChessMove";

export class ChessService {

   private Api = "http://localhost:8080/";

    public async startGame(): Promise<ChessGame>{
        return this.request('startGame');
    }

    public async endGame(): Promise<ChessGame>{
        return this.request('endGame');
    }

    public async getChessGame(): Promise<ChessGame>{
        return this.request('chessGame');
    }

    public async move(Position: number[][]): Promise<ChessMove>{
        return this.request('chessGame');
    }

    private async request(enpoint: any) {
        const res = await fetch(this.Api.concat(enpoint), { cache: 'no-store' });
        if (!res.ok) {
            throw new Error('Failed to fetch data');
        }
        return res.json();
    }
}
