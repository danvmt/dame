package dame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {
	Board b;
	Move move = new Move(2,1,3,0);

	@BeforeEach
	void setUp() throws Exception {
		 b = new Board();
		 b.placePieces();
	}

	@Test
	void InitialCheckersPlacementTest() {
		assertEquals(
		 "|  | R|  | R|  | R|  | R|\n"
		+"| R|  | R|  | R|  | R|  |\n"
		+"|  | R|  | R|  | R|  | R|\n"
		+"|  |  |  |  |  |  |  |  |\n"
		+"|  |  |  |  |  |  |  |  |\n"
		+"| B|  | B|  | B|  | B|  |\n"
		+"|  | B|  | B|  | B|  | B|\n"
		+"| B|  | B|  | B|  | B|  |\n", b.toString());
	}
	@Test
	void InfoAtFirstPositionTest() {
		assertEquals("EMPTY", b.getInfoAtPosition(0, 0).toString());
	}
	
	@Test
	void NotKingAfterOneMoveTest() {
		assertFalse(b.movePiece(move));
	}
	@Test
	void GetAllLegalMovesForColorTest() {	
		assertEquals("[current: (5, 0) + next: (4,1), current: (5, 2) + next: (4,3), current: (5, 2) + next: (4,1), current: (5, 4) + next: (4,5), current: (5, 4) + next: (4,3), current: (5, 6) + next: (4,7), current: (5, 6) + next: (4,5)]", b.getAllLegalMovesForColor(Board.color.BLACK).toString());
	}
	@Test
	void GetLegalMovesForColorAtPositionTest() {
		assertEquals("[current: (2, 1) + next: (3,2), current: (2, 1) + next: (3,0)]", b.getLegalMovesForColorAtPosition(Board.color.RED,2, 1).toString());
	}
	@Test
	void GetLegalMovesForPlayerTest() {
		assertEquals("[current: (2, 1) + next: (3,2), current: (2, 1) + next: (3,0)]", b.getLegalMovesForPlayer(2, 1).toString());
	}
	@Test
	void GetJumpsTest() {
		assertEquals("[]", b.getJumps(2, 1).toString());
	}
	

}
