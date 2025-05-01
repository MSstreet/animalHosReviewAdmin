import React from 'react';
import { Grid, Paper, Typography } from '@mui/material';

const ReviewListPage = () => {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Paper sx={{ p: 2 }}>
          <Typography variant="h5" gutterBottom>
            리뷰 목록
          </Typography>
          <Typography>
            동물병원 리뷰 목록이 여기에 표시됩니다.
          </Typography>
        </Paper>
      </Grid>
    </Grid>
  );
};

export default ReviewListPage; 