package view;

public enum TileColorPolicy {
	// Each node has its own color based on its elevation. The tile's color
	// is a gradient of all the node colors.
	BLEND_TILE_NODE_COLORS,
	// A tile's seed color (based on the seed's elevation) is assigned to all
	// nodes of the tile. This gives each tile a solid color based on the
	// elevation of the seed.
	ASSIGN_TILE_SEED_COLORS
}
