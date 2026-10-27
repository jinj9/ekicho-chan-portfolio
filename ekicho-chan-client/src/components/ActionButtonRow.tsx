import React from 'react';
import { Box, BoxProps } from '@mui/material';

const ActionButtonRow: React.FC<BoxProps> = ({ children, sx, ...props }) => (
  <Box
    {...props}
    sx={{
      display: 'flex',
      flexWrap: 'wrap',
      alignItems: 'center',
      justifyContent: 'flex-end',
      gap: 1.5,
      ...(sx as object),
    }}
  >
    {children}
  </Box>
);

export default ActionButtonRow;
