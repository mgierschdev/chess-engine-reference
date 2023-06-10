import {ChessGame} from "@/app/_models/ChessGame";
import {Position} from "@/app/_models/Position";
import {ChessboardMoveRequest} from "@/app/_models/ChessboardMoveRequest";
import {ChessPieceType} from "@/app/_models/enums";

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
        return await this.post('getValidMoves', position);
    }

    public async move(source: Position, target: Position): Promise<boolean>{
        const request: ChessboardMoveRequest = {
            source: source,
            target: target
        };
        const response = await this.post('move', request);
        return response.type == ChessPieceType.Empty;
    }

    private async post(endpoint: any, request: any){
        const res = await fetch(this.Api.concat(endpoint), {
            cache: 'no-store',
            method: 'POST',
            mode: 'cors',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request)
        });
        if(!res.ok){
            // throw new Error(('Failed to fetch data'));
        }
        return res.json();
    }

    private async get(endpoint: any) {
        const res = await fetch(this.Api.concat(endpoint), { cache: 'no-store' });
        if (!res.ok) {
            // throw new Error('Failed to fetch data');
        }
        return res.json();
    }
}
