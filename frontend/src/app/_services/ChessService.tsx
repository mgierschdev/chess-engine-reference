import {ChessGame} from "@/app/_models/ChessGame";
import {Position} from "@/app/_models/Position";

export class ChessService {

   private Api = "http://localhost:8080/";

    public async startGame(): Promise<ChessGame>{
        return this.get('startGame');
    }

    public async endGame(): Promise<ChessGame>{
        let response = await this.get('endGame');
        return this.get('endGame');
    }

    public async getChessGame(): Promise<ChessGame>{
        return this.get('chessGame');
    }

    public async getValidMoves(position: Position): Promise<Position[]>{
        return this.get('getValidMoves/'+position.row+"/"+position.col);
    }

    private async get(endpoint: any) {
        const res = await fetch(this.Api.concat(endpoint), { cache: 'no-store' });
        if (!res.ok) {
            throw new Error('Failed to fetch data');
        }
        return res.json();
    }
}
