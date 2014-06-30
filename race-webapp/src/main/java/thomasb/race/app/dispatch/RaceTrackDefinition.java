package thomasb.race.app.dispatch;

import java.util.Iterator;
import java.util.List;

import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceTrack;
import thomasb.race.engine.TrackPolygon;
import thomasb.race.engine.TrackSection;
import thomasb.race.engine.VectorPoint;

import com.google.common.collect.ImmutableList;

public enum RaceTrackDefinition implements RaceTrack {
	INSTANCE;
	
	private static final List<VectorPoint> FINISH = ImmutableList.of(new VectorPoint(20, 100),
			new VectorPoint(80, 100));
	
	private static final List<VectorPoint> CONTOUR = ImmutableList.of(
			new VectorPoint(0, 0),
			new VectorPoint(200, 0),
			new VectorPoint(200, 200),
			new VectorPoint(0, 200)
		);
	
	private static final List<VectorPoint> OUTER_WALL = ImmutableList.of(
			new VectorPoint(20, 20),
			new VectorPoint(180, 20),
			new VectorPoint(180, 180),
			new VectorPoint(20, 180)
		);
	
	private static final List<VectorPoint> OUTER_GREEN = ImmutableList.of(
			new VectorPoint(40, 40),
			new VectorPoint(160, 40),
			new VectorPoint(160, 160),
			new VectorPoint(40, 160)
		);
	
	private static final List<VectorPoint> INNER_GREEN = ImmutableList.of(
			new VectorPoint(60, 60),
			new VectorPoint(140, 60),
			new VectorPoint(140, 140),
			new VectorPoint(60, 140)
		);
	
	private static final List<VectorPoint> INNER_WALL = ImmutableList.of(
			new VectorPoint(80, 80),
			new VectorPoint(120, 80),
			new VectorPoint(120, 120),
			new VectorPoint(80, 120)
		);
	
	private static final List<TrackPolygon> TRACK_SECTIONS = ImmutableList.of(
			new TrackPolygon(INNER_WALL, TrackType.WALL),
			new TrackPolygon(INNER_GREEN, TrackType.GREEN),
			new TrackPolygon(OUTER_GREEN, TrackType.ASPHALT),
			new TrackPolygon(OUTER_WALL, TrackType.GREEN),
			new TrackPolygon(CONTOUR, TrackType.WALL)
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
			return yOffset(count) > 40;
		}
		
		@Override
		public VectorPoint next() {
			VectorPoint gridPoint = new VectorPoint(50, yOffset(count));
			count += 1;
			
			return gridPoint;
		}
		
		private double yOffset(int count) {
			return 99.0 - 5 * count;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
