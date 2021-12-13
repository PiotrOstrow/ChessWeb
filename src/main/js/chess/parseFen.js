

const parseFen = fen => {
    const map = new Map();

    const ranks = fen.split(' ')[0];

    let rank = 8;
    for(const r of ranks.split('/')) {
        const matches = r.matchAll(/(\d?)([kqrnbpKQRNBP])/g);

        let file = 0;
        for(const match of matches) {
            if(match[1]) {
                file += Number(match[1]);
            }
            const piece = parsePiece(match[2]);
            const coordinates = String.fromCharCode('a'.charCodeAt(0) + file++) + rank;
            map.set(coordinates, piece);
        }
        --rank;
    }

    return map;
}

const parsePiece = char => {
    switch(char) {
        case 'k': return 'black-king';
        case 'q': return 'black-queen';
        case 'r': return 'black-rook';
        case 'n': return 'black-knight';
        case 'b': return 'black-bishop';
        case 'p': return 'black-pawn';
        case 'K': return 'white-king';
        case 'Q': return 'white-queen';
        case 'R': return 'white-rook';
        case 'N': return 'white-knight';
        case 'B': return 'white-bishop';
        case 'P': return 'white-pawn';
    }
}

export default parseFen;