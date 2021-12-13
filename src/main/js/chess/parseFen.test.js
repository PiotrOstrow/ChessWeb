import parseFen from "./parseFen.js";


test('Parsing default starting position', () => {
    const actual = parseFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    expect(actual.get('a8')).toBe('black-rook');
    expect(actual.get('b8')).toBe('black-knight');
    expect(actual.get('c8')).toBe('black-bishop');
    expect(actual.get('d8')).toBe('black-queen');
    expect(actual.get('e8')).toBe('black-king');
    expect(actual.get('f8')).toBe('black-bishop');
    expect(actual.get('g8')).toBe('black-knight');
    expect(actual.get('h8')).toBe('black-rook');

    expect(actual.get('a1')).toBe('white-rook');
    expect(actual.get('b1')).toBe('white-knight');
    expect(actual.get('c1')).toBe('white-bishop');
    expect(actual.get('d1')).toBe('white-queen');
    expect(actual.get('e1')).toBe('white-king');
    expect(actual.get('f1')).toBe('white-bishop');
    expect(actual.get('g1')).toBe('white-knight');
    expect(actual.get('h1')).toBe('white-rook');

    for(let i = 0; i < 8; i++) {
        expect(actual.get(String.fromCharCode('a'.charCodeAt(0) + i) + 2)).toBe('white-pawn');
        expect(actual.get(String.fromCharCode('a'.charCodeAt(0) + i) + 7)).toBe('black-pawn');
    }
});

test('Parsing piece with prefix', () => {
    const actual = parseFen("7r/8/8/8/8/8/8/8 w KQkq - 0 1")

    expect(actual.get('h8')).toBe('black-rook');
})

test('Parsing multiple pieces with prefix in single rank', () => {
    const actual = parseFen("8/1p2Np2/8/8/8/8/8/8 w - - 0 1");

    expect(actual.get('b7')).toBe('black-pawn');
    expect(actual.get('e7')).toBe('white-knight');
    expect(actual.get('f7')).toBe('black-pawn');
})