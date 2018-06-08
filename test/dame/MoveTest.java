package dame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoveTest {
	Board b;
	Move move = new Move(2,1,3,0);
	
	@BeforeEach
	void setUp() throws Exception {
		 b = new Board();
		 b.placePieces();
	}

	@Test
	void GetSpaceInbetweenTest() {
		assertEquals("2=0", move.getSpaceInbetween().toString());
	}
	@Test
	void GetMoveAsStringTest() {
		assertEquals("current: (2, 1) + next: (3,0)", move.toString());
	}

}
