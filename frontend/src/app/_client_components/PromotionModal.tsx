import {ChessPieceType} from "@/app/_models/enums";

interface PromotionModalProps {
    onSelect: (piece: ChessPieceType) => void;
}

export default function PromotionModal({onSelect}: PromotionModalProps){
    return (
        <div className="promotion-modal">
            <div className="promotion-content">
                {[ChessPieceType.Queen, ChessPieceType.Rock, ChessPieceType.Bishop, ChessPieceType.Knight].map((type) => (
                    <button key={type} className="promotion-option" onClick={() => onSelect(type)}>
                        {type === ChessPieceType.Rock ? 'Rook' : type}
                    </button>
                ))}
            </div>
        </div>
    );
}
