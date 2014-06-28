package thomasb.race.app.dispatch;

import java.util.Iterator;
import java.util.List;

import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceTrack;
import thomasb.race.engine.TrackPolygon;
import thomasb.race.engine.TrackSection;
import thomasb.race.engine.TrackType;
import thomasb.race.engine.VectorPoint;

import com.google.common.collect.ImmutableList;

public enum RaceTrackDefinition implements RaceTrack {
	INSTANCE;
	
	private static final List<VectorPoint> FINISH = ImmutableList.of(new VectorPoint(2.0, 10.0),
			new VectorPoint(8.0, 10.0));
	
	private static final List<VectorPoint> CONTOUR = ImmutableList.of(
			new VectorPoint(0, 0),
			new VectorPoint(20, 0),
			new VectorPoint(20, 20),
			new VectorPoint(0, 20)
		);
	
	private static final List<VectorPoint> OUTER_WALL = ImmutableList.of(
			new VectorPoint(2, 2),
			new VectorPoint(18, 2),
			new VectorPoint(18, 18),
			new VectorPoint(2, 18)
		);
	
	private static final List<VectorPoint> OUTER_GREEN = ImmutableList.of(
			new VectorPoint(4, 4),
			new VectorPoint(16, 4),
			new VectorPoint(16, 16),
			new VectorPoint(4, 16)
		);
	
	private static final List<VectorPoint> INNER_GREEN = ImmutableList.of(
			new VectorPoint(6, 6),
			new VectorPoint(14, 6),
			new VectorPoint(14, 14),
			new VectorPoint(6, 14)
		);
	
	private static final List<VectorPoint> INNER_WALL = ImmutableList.of(
			new VectorPoint(8, 8),
			new VectorPoint(12, 8),
			new VectorPoint(12, 12),
			new VectorPoint(8, 12)
		);
	
	private static final List<TrackPolygon> TRACK_SECTIONS = ImmutableList.of(
			new TrackPolygon(CONTOUR, TrackType.WALL),
			new TrackPolygon(OUTER_WALL, TrackType.GREEN),
			new TrackPolygon(OUTER_GREEN, TrackType.ASPHALT),
			new TrackPolygon(INNER_GREEN, TrackType.GREEN),
			new TrackPolygon(INNER_WALL, TrackType.WALL)
		);
	
	@Override
	public Iterable<? extends PointDouble> getStartGrid() {
		return new Iterable<VectorPoint>() {
			@Override
			public Iterator<VectorPoint> iterator() {
				return new GridIterator();
			}
		};
	}
	
	@Override
	public List<? extends PointDouble> getContour() {
		return CONTOUR;
	}
	
	@Override
	public int getMaxLaps() {
		return 3;
	}
	
	@Override
	public List<? extends TrackSection> getSections() {
		return TRACK_SECTIONS;
	}
	
	@Override
	public List<? extends PointDouble> getFinish() {
		return FINISH;
	}
	
	private static class GridIterator implements Iterator<VectorPoint> {
		int count = 0;
		
		@Override
		public boolean hasNext() {
			return xOffset(count) > 4.0;
		}
		
		@Override
		public VectorPoint next() {
			VectorPoint gridPoint = new VectorPoint(xOffset(count), 0.0);
			count += 1;
			
			return gridPoint;
		}
		
		private double xOffset(int count) {
			return 9.9 - count / 10.0;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
