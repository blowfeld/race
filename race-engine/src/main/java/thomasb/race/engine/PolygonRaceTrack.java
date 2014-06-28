package thomasb.race.engine;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PolygonRaceTrack implements RaceTrack {
	private final List<TrackPolygon> trackSections;
	private final PointDouble finish1;
	private final PointDouble finish2;
	private final Iterable<PointDouble> gridPoints;
	private final int maxLaps;
	
	public PolygonRaceTrack(List<TrackPolygon> sections,
			PointDouble finish1, PointDouble finish2,
			Iterable<PointDouble> gridPoints,
			int maxLaps) {
		this.finish1 = finish1;
		this.finish2 = finish2;
		this.gridPoints = gridPoints;
		this.maxLaps = maxLaps;
		
		checkSectionsDontIntersect(sections);
		Builder<TrackPolygon> unique = ImmutableList.builder();
		TrackPolygon previousTrackType = sections.get(0);
		for (int i = 0; i < sections.size(); i++) {
			if (!sections.get(i).getType().equals(previousTrackType)) {
				unique.add(sections.get(i));
			}
		}
		
		this.trackSections = unique.build();
	}
	
	private void checkSectionsDontIntersect(List<TrackPolygon> sections) {
		// TODO check sections contain each other
	}

	@Override
	public List<TrackSegment> segmentsFor(PointDouble startPoint, int direction) {
		return new TrackSegmentCalculator(trackSections, finish1, finish2).segmentsFor(startPoint, direction);
	}
	
	@Override
	public Iterable<PointDouble> getStartGrid() {
		return gridPoints;
	}
	
	@Override
	public List<PointDouble> getContour() {
		return trackSections.get(0).getCorners();
	}
	
	@Override
	public int getMaxLaps() {
		return maxLaps;
	}
}
