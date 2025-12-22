import {ChessGame} from "@/app/_models/ChessGame";
import {Position} from "@/app/_models/Position";
import {ChessboardMoveRequest} from "@/app/_models/ChessboardMoveRequest";
import {ChessPieceType, Color} from "@/app/_models/enums";

export class ChessService {

   private Api = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/";

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

    public async move(source: Position, target: Position, promotionType: ChessPieceType = ChessPieceType.Queen): Promise<boolean>{
        const request: ChessboardMoveRequest = {
            source: source,
            target: target,
            promotionType: promotionType
        };
        const response = await this.post('move', request);
        // Move is successful if we get a valid response with chessPiece property
        // The chessPiece represents what was at the target square (Empty for regular moves, or the captured piece for captures)
        return response && response.chessPiece != null;
    }

    public async getAIMove(): Promise<{from: Position, to: Position, score: number} | null> {
        try {
            const response = await this.get('aiMove');
            if (!response || !response.content) {
                return null;
            }
            
            // Parse the response format: "fromRow,fromCol,toRow,toCol,score"
            const parts = response.content.split(',');
            if (parts.length >= 4) {
                return {
                    from: { row: parseInt(parts[0]), col: parseInt(parts[1]) },
                    to: { row: parseInt(parts[2]), col: parseInt(parts[3]) },
                    score: parts.length > 4 ? parseInt(parts[4]) : 0
                };
            }
            return null;
        } catch (error) {
            console.error("Failed to get AI move:", error);
            return null;
        }
    }

    private async post(endpoint: any, request: any){
        try {
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
        } catch (error) {
            console.error("Failed to fetch:", error);
            return null;
        }
    }

    private async get(endpoint: any) {
        try {
            const res = await fetch(this.Api.concat(endpoint), { cache: 'no-store' });
            if (!res.ok) {
                // throw new Error('Failed to fetch data');
            }
            return res.json();
        } catch (error) {
            console.error("Failed to fetch:", error);
            return null;
        }
    }
}
