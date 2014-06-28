package thomasb.race.engine;

import java.util.List;

public class PolygonRaceTrack implements RaceTrack {
	private final List<? extends TrackSection> trackSections;
	private final List<? extends PointDouble> finish;
	private final Iterable<PointDouble> gridPoints;
	private final int maxLaps;
	
	public PolygonRaceTrack(List<? extends TrackSection> sections,
			List<? extends PointDouble> finish,
			Iterable<PointDouble> gridPoints,
			int maxLaps) {
		this.finish = finish;
		this.gridPoints = gridPoints;
		this.maxLaps = maxLaps;
		
		checkSectionsDontIntersect(sections);
		this.trackSections = sections;
	}
	
	private void checkSectionsDontIntersect(List<? extends TrackSection> sections) {
		// TODO check sections contain each other
	}
	
	@Override
	public Iterable<? extends PointDouble> getStartGrid() {
		return gridPoints;
	}
	
	@Override
	public List<? extends PointDouble> getContour() {
		return trackSections.get(0).getCorners();
	}
	
	@Override
	public int getMaxLaps() {
		return maxLaps;
	}

	@Override
	public List<? extends TrackSection> getSections() {
		return trackSections;
	}

	@Override
	public List<? extends PointDouble> getFinish() {
		return finish;
	}
}
