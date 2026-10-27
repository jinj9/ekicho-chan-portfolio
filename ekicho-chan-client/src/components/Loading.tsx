import React from 'react';
import {
  Backdrop,
  CircularProgress,
} from '@mui/material'

const Loading = () => {
  return (
    <Backdrop className={"styles.backdrop"} open={true} sx={{zIndex: 100}}>
      <CircularProgress color="primary" size={100} thickness={5.0} />
    </Backdrop>
  )
}

export default Loading;
