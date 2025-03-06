# Signals

Signals hold value of particular type and are identified by its
owning widget and name. Name is unique within owning widget.

Widgets tracks signals that has values that widget uses for its actualization.

Once signal changes, all widgets depending on signal are notified and updated. 
During widget update another signal may change and update process continues until 
there is no "dirty" widget.

After signal changes are handled, affected widgets are redrawn.
